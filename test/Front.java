package cms_tests.objects.data;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public enum Front {

    DESKTOP     ("Desktop",     "desktop",   "desktop_site",    true ),
    MOBILE      ("Mobile",      "mobile",    "mobile_site",     false),
    MOBILE_APP  ("Mobile App",  "mobileApp", "mobile_app",      false),
    ADAPTIVE    ("Adaptive",    "adaptive",  "adaptive_site",   false),
    KIOSK       ("Kiosk",       "kiosk",     "kiosk_app",       false),
    DEFAULT     (DESKTOP),
    ALWAYS;

    Front(String name, String value, String swaggerMark, boolean main) {
        this.name        = name;
        this.value       = value;
        this.swaggerMark = swaggerMark;
    }
    Front(Front clone) {
        this.name        = clone.getName();
        this.value       = clone.getValue();
        this.swaggerMark = clone.getSwaggerMark();
    }
    Front () {}
    public enum Parameter {NAME, VALUE, SWAGGER_MARK;}

    @Getter
    private String name;
    @Getter
    private String value;
    @Getter
    private String swaggerMark;
    @Getter @Setter
    private boolean main;
    @Getter @Setter
    private boolean enabled = false;
    @Getter @Setter
    private boolean active = false;
    @Getter @Setter
    private Map<String, Boolean> map = new HashMap<>();

    public static List<Front> getFronts() {
        List<Front> list = new ArrayList<>(EnumSet.allOf(Front.class));
        return list.stream().filter(f -> !f.equals(ALWAYS)).collect(Collectors.toList());
    }

    public static Front mainUnit() {
        return getFronts().stream().filter(f -> f.main).findAny().orElse(DEFAULT);
    }

    public static Front activeUnit() {
        return getFronts().stream().filter(f -> f.active).findAny().orElse(DEFAULT);
    }

    public static Front filter(String key) {
        Optional<Front>         front = getFronts().stream().filter(f -> f.getName().equals(key)).findAny();
        if (!front.isPresent()) front = getFronts().stream().filter(f -> f.getValue().equals(key)).findAny();;
        if (!front.isPresent()) front = getFronts().stream().filter(f -> f.getSwaggerMark().equals(key)).findAny();
        return front.orElse(ALWAYS);
    }

    public static String getAttribute(Parameter type, String key) {
        switch (type) {
            case NAME:
                return Front.filter(key).getName();
            case VALUE:
                return Front.filter(key).getValue();
            case SWAGGER_MARK:
                return Front.filter(key).getSwaggerMark();
            default:
                return null;
        }
    }
}
