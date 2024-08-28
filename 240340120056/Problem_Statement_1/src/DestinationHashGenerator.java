package org.json;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

    private static final int RANDOM_STRING_LENGTH = 8;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <240340120056> <C:/Users/VICTUS/.m2/repository/org/json/json/20210307/json-20210307-sources.jar>");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            // Read JSON file
            FileReader fileReader = new FileReader(jsonFilePath, StandardCharsets.UTF_8);
            JSONTokener tokener = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);

            // Find destination value
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in JSON.");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(RANDOM_STRING_LENGTH);

            // Generate MD5 hash
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Print the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        return findDestinationValueRecursive(jsonObject);
    }

    private static String findDestinationValueRecursive(Object json) {
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            if (jsonObject.has("destination")) {
                return jsonObject.getString("destination");
            }
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                String result = findDestinationValueRecursive(value);
                if (result != null) {
                    return result;
                }
            }
        } else if (json instanceof org.json.JSONArray) {
            org.json.JSONArray jsonArray = (org.json.JSONArray) json;
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                String result = findDestinationValueRecursive(value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
