package wooteco.subway.ui.dto;

public class LineRequest {

    private String name;
    private String color;

    private LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "LineUpdateRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
