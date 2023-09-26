package com.TraducteurProject.Traducteur;

import java.io.*;
import java.util.*;
import java.awt.FileDialog;
import java.awt.Frame;


// improt JSONArray.class ibrairy from ./json folder in local directory






public class Traducteur {
    public static void main(String[] args) {
        String tradPath = "src/main/resources/static/traducteur";
        String cliPath = "src/main/resources/static/commande";

        // while tradPath and cliPath are empty ask the user to choose the files
       /* while (tradPath.isEmpty()) {
                // get the path of the trad file by choosing the file
                try {
                    FileDialog dialog = new FileDialog((Frame) null, "Selectionner le fichier Traducteur");
                    dialog.setMode(FileDialog.LOAD);
                    dialog.setVisible(true);
                    tradPath = dialog.getDirectory() + dialog.getFile();
                } catch (Exception e) {
                    System.out.println("Aucun fichier selectionne");

                }
        }

        while (cliPath.isEmpty()) {
            // get the path of the cli file by choosing the file
            try {
                FileDialog dialog = new FileDialog((Frame) null, "Selectionner le fichier Comande ");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);
                cliPath = dialog.getDirectory() + dialog.getFile();
            } catch (Exception e) {
                System.out.println("Aucun fichier selectionne");
            }
        }

        */




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
            

            String result_path="";

            // save the results in a text file
            try {
                // choose the path where to save the file as json data  withourt using JSON librairy
                FileDialog dialog = new FileDialog((Frame) null, "Enregistrer le fichier");
                dialog.setMode(FileDialog.SAVE);
                dialog.setVisible(true);
                result_path = dialog.getDirectory() + dialog.getFile();
                File myObj = new File(result_path);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }

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
}
