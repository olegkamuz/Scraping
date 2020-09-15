package scraping;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {
    private String title;
    private BigDecimal price;
    private String url;
}
