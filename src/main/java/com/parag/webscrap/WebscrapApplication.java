package com.parag.webscrap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebscrapApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebscrapApplication.class, args);
		String url = "https://challenge.longshotsystems.co.uk/go";
		String submitUrlTemplate = "https://challenge.longshotsystems.co.uk/submitgo?answer=%s&name=YourName";

		try {
			// Fetch the page with session handling
			Connection.Response initialResponse = Jsoup.connect(url).method(Connection.Method.GET).execute();
			Document document = initialResponse.parse();
			System.out.println("Page fetched successfully.");

			// Extract data attributes
			Elements numberBoxes = document.select(".number-box");
			List<String> dataAttributes = new ArrayList<>();
			for (Element box : numberBoxes) {
				String dataAttr = box.attr("data");
				dataAttributes.add(dataAttr);
			}
			System.out.println("Data attributes extracted: " + dataAttributes);

			// Extract numbers
			String numbersStr = document.select(".number-panel").text();
			String[] numbers = numbersStr.split("\\s+");
			String answer = String.join("", numbers);
			System.out.println("Extracted Numbers: " + answer);

			// Compute the generated hash
			String generatedHash = gen(answer);
			System.out.println("Generated Hash: " + generatedHash);

			// Determine the box index and selected data
			int boxIndex = Integer.parseInt(answer) % dataAttributes.size();
			System.out.println(dataAttributes.size());
			String selectedData = dataAttributes.get(boxIndex);
			System.out.println("Box Index: " + boxIndex);
			System.out.println("Selected Data: " + selectedData);

			// Construct the final hash
			String finalHash = generatedHash + selectedData;
			String submitUrl = String.format(submitUrlTemplate, finalHash);
			System.out.println("Submit URL: " + submitUrl);

			// Submit the URL and check the response
			String response = submitForm(submitUrl, initialResponse.cookies());
			System.out.println("Response: " + response);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String submitForm(String submitUrl, Map<String, String> cookies) throws IOException {
		Connection.Response response = Jsoup.connect(submitUrl).cookies(cookies).method(Connection.Method.GET)
				.execute();
		return response.body();
	}

	private static String gen(String inputStr) {
		// Construct the final hash
		String reversedInput = new StringBuilder(inputStr).reverse().toString();
		String combinedInput = inputStr + reversedInput;
		long cInt = Long.parseLong(combinedInput);

		long hashedValue = (((cInt % 5) + 99) * (cInt % 5));
		return (combinedInput + hashedValue);
	}
}
