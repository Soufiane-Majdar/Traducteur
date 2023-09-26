package com.TraducteurProject.Traducteur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.*;
import java.awt.FileDialog;
import java.awt.Frame;

@RestController
@SpringBootApplication
public class TraducteurApplication {

	// get 2 files from a form  post mthode on url api/upload and return the name of the files
	@RequestMapping(value = "/api/upload", method = RequestMethod.POST)
	public @ResponseBody
	String uploadFile(@RequestParam("file1") MultipartFile filel,@RequestParam("file2") MultipartFile file2,@RequestParam("file3")  String file3) {

		if (filel.isEmpty() || file2.isEmpty() ) {
			return "please select a file!";
		}

		
		 // save file to static folder
		try {
			byte[] bytes1 = filel.getBytes();
			java.nio.file.Path path1 = java.nio.file.Paths.get("src/main/resources/static/traducteur");
			java.nio.file.Files.write(path1, bytes1);

			byte[] bytes2 = file2.getBytes();
			java.nio.file.Path path2 = java.nio.file.Paths.get("src/main/resources/static/commande");
			java.nio.file.Files.write(path2, bytes2);

			// execute main function from Traducteur.java
			// get auregin path of the file file3
			String result_path = file3;
			System.out.println(result_path);
			taducteur(result_path+"/result.json");

		} catch (Exception e) {
			e.printStackTrace();
		}



		return "upload success";

	}

	@RequestMapping("/api/upload")
	public String uploadFile() {
		return "upload";
	}

	// get 2 files from a form  post mthode on url api/upload and return the name of the files


	public  void taducteur(String result_path) {
		String tradPath = "src/main/resources/static/traducteur";
		String cliPath = "src/main/resources/static/commande";



		Map<String, List<Column>> translator = new HashMap<>();

		try (BufferedReader tradReader = new BufferedReader(new FileReader(tradPath));
			 BufferedReader cliReader = new BufferedReader(new FileReader(cliPath))) {

			List<String> cliLines = new ArrayList<>();
			String cliLine;
			while ((cliLine = cliReader.readLine()) != null) {
				cliLines.add(cliLine);
			}

			for (String tradLine = tradReader.readLine(); tradLine != null; tradLine = tradReader.readLine()) {
				String[] parts = tradLine.split("\\s+");
				if (parts.length >= 6) {
					String delimiter = parts[2].trim();
					int startPosition = Integer.parseInt(parts[3]);
					int length = Integer.parseInt(parts[4]);

					String columnName = "";
					for (String cliLineItem : cliLines) {
						if (cliLineItem.contains(delimiter)) {
							// scape if startPosition == length or length equal to 0
							if (length == 0 || startPosition == length) {
								System.out.println("columnName: is empty");
							} else {
								columnName = cliLineItem.substring(startPosition - 1, (startPosition + length) - 1)
										.trim();
								Column column = new Column(columnName, startPosition, length);
								translator.computeIfAbsent(delimiter, k -> new ArrayList<>()).add(column);
								// System.out.println("columnName: " + columnName);
								break;
							}
						}
					}
				}
			}

			for (Map.Entry<String, List<Column>> entry : translator.entrySet()) {
				System.out.println("Delimiter: " + entry.getKey());
				for (Column column : entry.getValue()) {
					System.out.println("   " + column);
				}
			}




			// save the results in a text file
			try {




				// write the result in the file using the json format using Map and toString method from Column class
				FileWriter myWriter = new FileWriter(result_path);
				myWriter.write("{\n");
				for (Map.Entry<String, List<Column>> entry : translator.entrySet()) {
					myWriter.write("    \"" + entry.getKey() + "\": [\n");
					for (Column column : entry.getValue()) {
						// if the column is the last one in the list
						if(column == entry.getValue().get(entry.getValue().size()-1))
							myWriter.write("        " + column.toString() + "\n");
						else
							myWriter.write("        " + column.toString() + ",\n");
					}
					// if the delimiter is the last one in the map
					if(entry.getKey() == translator.keySet().toArray()[translator.size()-1])
						myWriter.write("    ]\n");
					else
						myWriter.write("    ],\n");
				}
				myWriter.write("}");
				myWriter.close();
				System.out.println("Successfully wrote to the file.");



			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(TraducteurApplication.class, args);
	}




}
