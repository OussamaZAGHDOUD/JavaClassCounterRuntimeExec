package app;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {
	// Add comment
	//
	// instance of a private member inner class declared later in this class
	CalculateFilesHelper helper = new CalculateFilesHelper();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		

		primaryStage.setTitle("Java Class Counter");
		Font font = Font.font("Comic Sans MS", FontWeight.BOLD, 16);
		//String fontName = "Avenir Next";

		VBox root = new VBox();
		root.setPadding(new Insets(20));
		root.setAlignment(Pos.CENTER);

		HBox first = new HBox(10);
		first.setPadding(new Insets(10));
		first.setAlignment(Pos.CENTER);

		HBox second = new HBox(10);
		second.setPadding(new Insets(10));
		second.setAlignment(Pos.CENTER);

		Label label = new Label("Path : ");
		label.setTextFill(Color.web("#044E54"));
		label.setFont(font);
		TextField txtPath = new TextField();
		txtPath.setText("");
		txtPath.setMinWidth(370);
		Button btnCalculate = new Button("Count the number of Java class");
		Button btnAgain = new Button("Again ?");

		btnCalculate.setCursor(Cursor.OPEN_HAND);
		btnCalculate.setFont(font);
		btnAgain.setCursor(Cursor.OPEN_HAND);
		btnAgain.setFont(font);
		Label labelNumberOfClasses = new Label(" * ");
		labelNumberOfClasses.setTextFill(Color.web("#044E54"));
		// Label labelNumberOfClasses = new Label("The number of Java classes in the
		// given directory is equal to : ");

		ImageView findImageView = new ImageView(getClass().getResource("/resources/images/f1.png").toString());
		ImageView findImageViewModified = new ImageView(
				getClass().getResource("/resources/images/f1Mouse.png").toString());

		// btnCalculate.setGraphic(findImageView);
		btnCalculate.graphicProperty()
				.bind(Bindings.when(btnCalculate.hoverProperty()).then(findImageView).otherwise(findImageViewModified));
		btnCalculate.setContentDisplay(ContentDisplay.RIGHT);

		ImageView againImageView = new ImageView(getClass().getResource("/resources/images/again.png").toString());
		ImageView againImageViewModified = new ImageView(
				getClass().getResource("/resources/images/againMouse.png").toString());
		// btnAgain.setGraphic(againImageView);
		btnAgain.graphicProperty()
				.bind(Bindings.when(btnAgain.hoverProperty()).then(againImageView).otherwise(againImageViewModified));

		btnAgain.setContentDisplay(ContentDisplay.RIGHT);

		primaryStage.setResizable(false);

		first.getChildren().addAll(label, txtPath);
		second.getChildren().addAll(btnCalculate, btnAgain);
		// root.getChildren().addAll(label, txtPath, btnCalculate, btnAgain,
		// labelNumberOfClasses);

		Label titleLabel = new Label("The java class Counter");
		titleLabel.setFont(Font.font("Pacifico", FontWeight.BOLD, 28));
		titleLabel.setTextFill(Color.web("#044E54"));
		titleLabel.setPadding(new Insets(0, 0, 40, 0));
		//btnAgain.setBackground((new Background(new BackgroundFill(Color.web("#E66A6A"), null, null))));
		btnAgain.setTextFill(Color. DARKBLUE);
		btnAgain.setPadding(new Insets(8));
		//btnCalculate.setBackground((new Background(new BackgroundFill(Color.web("#E66A6A"), null, null))));
		btnCalculate.setTextFill(Color.DARKBLUE);
		btnCalculate.setPadding(new Insets(8));

		root.setBackground(new Background(new BackgroundFill(Color.web("#F0F4F8"), null, null)));
		root.getChildren().addAll(titleLabel, first, labelNumberOfClasses, second);

		btnAgain.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.runLater(() -> {
					btnCalculate.setDisable(false);
					labelNumberOfClasses.setText(" * ");
				});
			}
		});

		btnCalculate.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				btnAgain.setDisable(true);
				btnCalculate.setDisable(true);

				ExecutorService executorService = Executors.newSingleThreadExecutor();
				executorService.submit(() -> {
					System.out.println("Thread inside the Java Counter program : "+Thread.currentThread().getId());
					Path path = Paths.get(txtPath.getText());
					// System.out.println(" le path est = " + txtPath.getText());
					if (path != null && !path.toString().equals("") && Files.isDirectory(path)) {
						Long numberOfFiles = helper.calculateFiles(".java", path);
						helper.storeDataToFile(".java", path);

						Platform.runLater(() -> {
							System.out.println("Thread inside the Java counter : "+Thread.currentThread().getId());

							labelNumberOfClasses.setText(
									"The number of Java classes in the given directory is equal to : " + numberOfFiles);
							btnAgain.setDisable(false);

						});

					} else {
						Platform.runLater(() -> {
							labelNumberOfClasses.setText("Invalid path  !");
							btnAgain.setDisable(false);

						});
					}
				});

				executorService.shutdown();
			}
		});

		Scene scene = new Scene(root, 600, 400);
		primaryStage.setScene(scene);
		labelNumberOfClasses.setFont(font);

		primaryStage.show();
	}

	private class CalculateFilesHelper {

		// calculate the number of files with a given extension in a given path
		public Long calculateFiles(String extension, Path path) {
			Long numberOfFiles = 0L;
			try {
				numberOfFiles = Files.walk(path).filter(p -> p.toAbsolutePath().toString().endsWith(extension)).count();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return numberOfFiles;

		}

		// store in a file that will be created in the current working directory
		public void storeDataToFile(String extension, Path path) {
			Path pathToWriteData = Paths.get("./fileToStoreData.txt");
			pathToWriteData = pathToWriteData.toAbsolutePath();
			try (BufferedWriter bufferedWriter = Files.newBufferedWriter(pathToWriteData, StandardCharsets.UTF_8)) {

				Files.walk(path).filter(p -> p.toAbsolutePath().toString().endsWith(extension))
						.map(p -> p.toString().substring(p.toString().lastIndexOf("\\") + 1)).forEach(element -> {
							try {
								bufferedWriter.write(element + System.getProperty("line.separator"));
							} catch (IOException e) {
								e.printStackTrace();
							}
						});

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
