package com.wordgame.model;

import java.util.Collection;

public class SearchForm {

    private String startChar;

    private String endChar;


    private String length;

    private Collection<String> words;

    public String getStartChar() {
        return startChar;
    }

    public void setStartChar(String startChar) {
        this.startChar = startChar;
    }

    public String getEndChar() {
        return endChar;
    }

    public void setEndChar(String endChar) {
        this.endChar = endChar;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Collection<String> getWords() {
        return words;
    }

    public void setWords(Collection<String> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (startChar != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("startChar=[").append(startChar).append(']');
        }
        if (endChar != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("endChar=[").append(endChar).append(']');
        }
        if (length != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("length=[").append(length).append(']');
        }
        if (words != null) {
            sb.append(sb.length() == 0 ? "" : ", ").append("words=[").append(words).append(']');
        }
        return sb.toString();
    }

}
