package com.TraducteurProject.Traducteur;

class Column {
    String name;
    int startPosition;
    int length;

    public Column(String name, int startPosition, int length) {
        this.name = name;
        this.startPosition = startPosition;
        this.length = length;
    }

    @Override
    public String toString() {
        // return json format
        return "{ \"name\": \"" + name + "\", \"startPosition\": " + startPosition + ", \"length\": " + length
                + " }";
    }
}
