/*
 * Name:        Rakesh Shrestha
 * Course:      CSD420-T301 Advanced Java Programming
 * Instructor:  Professor Tedi Pano
 * Assignment:  Module 11.2 Programming Assignment
 * Date:        November 22, 2026
 *
 * Purpose:
 *   Additional Java JSON example for the Jackson API (not included in my paper).
 *   My paper shows Jackson's data binding model (ObjectMapper and a POJO).
 *   This program shows the lower level Streaming API from jackson-core:
 *     1. JsonGenerator writes a JSON document one token at a time to a file.
 *     2. JsonParser reads the same file back one token at a time.
 *     3. A second, partial read stops as soon as it finds the field it needs,
 *        so the rest of the document is never processed.
 *
 * Source of the idea:
 *   This example is adapted from the "Jackson Streaming API" tutorial on
 *   Baeldung (https://www.baeldung.com/jackson-streaming-api). The original
 *   writes and reads a small "person" object (name, age, address array) and
 *   then shows a partial read of the age field. I changed the data to a
 *   course record, wrote the output to a file instead of a byte stream,
 *   added the nested object and boolean value, and added the comments.
 *
 * Library:
 *   Jackson 2.21.8 (LTS). Only jackson-core is needed for this program, but the
 *   full set of JARs in the lib folder is:
 *     jackson-core-2.21.8.jar, jackson-databind-2.21.8.jar, jackson-annotations-2.21.jar
 *
 * Compile and run from the folder that holds this file (Windows PowerShell):
 *   javac -cp "lib/*" JacksonStreamingExample.java
 *   java  -cp "lib/*;." JacksonStreamingExample
 *   (On macOS or Linux, use ":" instead of ";" in the run classpath.)
 */

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JacksonStreamingExample {

    // Name of the JSON file this program creates and then reads.
    private static final String FILE_NAME = "course_record.json";

    public static void main(String[] args) {
        // One JsonFactory can be reused. It creates both parsers and generators.
        JsonFactory factory = new JsonFactory();
        File jsonFile = new File(FILE_NAME);

        try {
            writeCourseRecord(factory, jsonFile);
            System.out.println("Step 1: JSON written to " + jsonFile.getAbsolutePath());

            System.out.println();
            System.out.println("Step 2: Full streaming read");
            readCourseRecord(factory, jsonFile);

            System.out.println();
            System.out.println("Step 3: Partial streaming read");
            String instructor = findInstructor(factory, jsonFile);
            System.out.println("Instructor found early: " + instructor);
        } catch (IOException e) {
            // Streaming calls in Jackson 2.x throw IOException (JsonParseException
            // and JsonGenerationException are subclasses of it).
            System.err.println("JSON processing failed: " + e.getMessage());
        }
    }

    /**
     * Writes a course record to the file using JsonGenerator.
     * Every start call (object or array) must be matched with an end call.
     *
     * Output written (formatted here for readability):
     * {
     *   "course" : "CSD420",
     *   "title" : "Advanced Java Programming",
     *   "credits" : 3,
     *   "online" : true,
     *   "instructor" : { "name" : "Tedi Pano", "role" : "Professor" },
     *   "topics" : [ "JavaFX", "Generics", "Collections", "JDBC", "JSON" ]
     * }
     */
    private static void writeCourseRecord(JsonFactory factory, File file) throws IOException {
        // try-with-resources closes the generator, which also flushes the file.
        try (JsonGenerator gen = factory.createGenerator(file, JsonEncoding.UTF8)) {
            gen.useDefaultPrettyPrinter(); // indent the output so it is easy to read

            gen.writeStartObject();                                   // {
            gen.writeStringField("course", "CSD420");
            gen.writeStringField("title", "Advanced Java Programming");
            gen.writeNumberField("credits", 3);
            gen.writeBooleanField("online", true);

            gen.writeObjectFieldStart("instructor");                  // "instructor": {
            gen.writeStringField("name", "Tedi Pano");
            gen.writeStringField("role", "Professor");
            gen.writeEndObject();                                     // }

            gen.writeArrayFieldStart("topics");                       // "topics": [
            String[] topics = {"JavaFX", "Generics", "Collections", "JDBC", "JSON"};
            for (String topic : topics) {
                gen.writeString(topic);
            }
            gen.writeEndArray();                                      // ]

            gen.writeEndObject();                                     // }
        }
    }

    /**
     * Reads the whole file token by token with JsonParser.
     * nextToken() moves forward one token; currentName() gives the field name
     * the parser is on, and the getXxx() methods return the value.
     */
    private static void readCourseRecord(JsonFactory factory, File file) throws IOException {
        try (JsonParser parser = factory.createParser(file)) {

            // The first token must be START_OBJECT because the file starts with {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Expected the JSON document to start with an object.");
            }

            String course = null;
            String title = null;
            int credits = 0;
            boolean online = false;
            String instructorName = null;
            List<String> topics = new ArrayList<>();

            // Loop through the fields of the top-level object.
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName(); // we are on a FIELD_NAME token
                parser.nextToken();                  // move to the field's value

                switch (field) {
                    case "course":
                        course = parser.getText();
                        break;
                    case "title":
                        title = parser.getText();
                        break;
                    case "credits":
                        credits = parser.getIntValue();
                        break;
                    case "online":
                        online = parser.getBooleanValue();
                        break;
                    case "instructor":
                        // Nested object: read its own fields until its END_OBJECT.
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            String innerField = parser.currentName();
                            parser.nextToken();
                            if ("name".equals(innerField)) {
                                instructorName = parser.getText();
                            }
                            // "role" is read past but not stored
                        }
                        break;
                    case "topics":
                        // Array: collect each string until END_ARRAY.
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            topics.add(parser.getText());
                        }
                        break;
                    default:
                        // Unknown field: skip its value, even if it is an object or array.
                        parser.skipChildren();
                }
            }

            System.out.println("Course:     " + course + " - " + title);
            System.out.println("Credits:    " + credits);
            System.out.println("Online:     " + online);
            System.out.println("Instructor: " + instructorName);
            System.out.println("Topics:     " + String.join(", ", topics));
        }
    }

    /**
     * Partial read: returns the instructor's name and stops right away.
     * The "topics" array that comes after it is never parsed, which is the
     * main speed advantage of the Streaming API on large documents.
     */
    private static String findInstructor(JsonFactory factory, File file) throws IOException {
        try (JsonParser parser = factory.createParser(file)) {
            int tokensRead = 0;
            JsonToken token;

            while ((token = parser.nextToken()) != null) {
                tokensRead++;
                if (token == JsonToken.FIELD_NAME && "name".equals(parser.currentName())) {
                    parser.nextToken();
                    tokensRead++;
                    System.out.println("Tokens read before stopping: " + tokensRead);
                    return parser.getText();
                }
            }
        }
        return "not found";
    }
}
