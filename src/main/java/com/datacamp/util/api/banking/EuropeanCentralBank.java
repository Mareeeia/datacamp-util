package com.datacamp.util.api.banking;

import org.w3c.dom.Element;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

/** Fetches ECB daily euro reference rates. Rates update 16:00 CET every working day. */
public class EuropeanCentralBank {

    private static final String ECB_URL =
            "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private Map<String, Double> fetchTable() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(ECB_URL)).GET().build();
            String xml = null;
            xml = HTTP.send(req, HttpResponse.BodyHandlers.ofString()).body();

            var doc =
                    DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(new java.io.ByteArrayInputStream(xml.getBytes()));

            Map<String, Double> rates = new HashMap<>();
            rates.put("EUR", 1.0); // base currency

            var cubes = doc.getElementsByTagName("Cube");
            for (int i = 0; i < cubes.getLength(); i++) {
                Element el = (Element) cubes.item(i);
                if (el.hasAttribute("currency")) {
                    rates.put(el.getAttribute("currency"), Double.valueOf(el.getAttribute("rate")));
                }
            }
            return rates;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private volatile Map<String, Double> cache; // naïve 1-run cache

    public double getRateEuroTo(String target) {
        if (cache == null) cache = fetchTable();
        Double rate = cache.get(target.toUpperCase());
        if (rate == null) throw new IllegalArgumentException("Currency not in ECB list: " + target);
        return rate;
    }
}
