package crawler.model;

import lombok.*;

/**
 * [
 * {
 * "id": "corona",
 * "category": "topics",
 * "hits": 500000,
 * "_rid": "3f00ANi1TPIBAAAAAAAAAA==",
 * "_self": "dbs/3f00AA==/colls/3f00ANi1TPI=/docs/3f00ANi1TPIBAAAAAAAAAA==/",
 * "_etag": "\"4601d766-0000-0d00-0000-5e75f6060000\"",
 * "_attachments": "attachments/",
 * "_ts": 1584788998
 * },
 * {
 * "category": "topics",
 * "id": "ausgangsperre",
 * "_rid": "3f00ANi1TPIDAAAAAAAAAA==",
 * "_self": "dbs/3f00AA==/colls/3f00ANi1TPI=/docs/3f00ANi1TPIDAAAAAAAAAA==/",
 * "_etag": "\"4601e26f-0000-0d00-0000-5e75f68c0000\"",
 * "_attachments": "attachments/",
 * "_ts": 1584789132
 * }
 * ]
 */
@Data
@Builder
public class Thema {

    private String id;
    private String category;
    private Long hits;
    private String text;
    private String source;
}
