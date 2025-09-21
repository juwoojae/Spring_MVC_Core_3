package hello.itemservice.domain.item;

public enum ItemType {
    BOOK("도서"), FOOD("식품"), ETC("기타");

    private final String description; //설명

    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
