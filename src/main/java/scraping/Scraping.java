package scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

public class Scraping {
    public static void main(String[] args) {
        String searchQuery = "Philips AVENT";
        String baseUrl = "https://www.kidsroom.de/";
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = null;
        try {
            String searchUrl = "https://www.kidsroom.de/ru/?query=" + URLEncoder.encode(searchQuery, "UTF-8") + "&s=1";
            page = client.getPage(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (page != null) {
            List<Object> itemsToCast = page.getByXPath("//div[contains(@class,'ProductInfo')]");
            List<HtmlElement> items = itemsToCast.stream()
                    .filter(element -> element instanceof HtmlElement)
                    .map(element -> (HtmlElement) element)
                    .collect(Collectors.toList());
            if (items.isEmpty()) {
                System.out.println("No items found!");
            } else {
                for (HtmlElement htmlItem : items) {
                    HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//div[contains(@class,'Cat_ProductName')]//a"));
                    HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//span[contains(concat(' ', @class, ' '), ' Price ')]"));

                    String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText();

                    Item item = new Item();
                    item.setTitle(itemAnchor.asText());
                    item.setUrl(baseUrl + itemAnchor.getHrefAttribute());
                    item.setPrice(new BigDecimal(itemPrice.replaceAll("[$|€|*| ]", "").replaceAll("[,]",".")));

                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = null;
                    try {
                        jsonString = mapper.writeValueAsString(item);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    System.out.println(jsonString);
                }
            }
        } else {
            System.out.println("no page found");
        }
    }

}
