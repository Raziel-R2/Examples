package cms_tests.objects.pages.add;

import cms_tests.objects.pages.interaction.Hub;
import io.qameta.allure.Step;

interface PushButton<T extends AddPage> extends Hub {

    T add();

    @Step("нажимаем на кнопку {webForm}")
    default void pushButton(String webForm) {
        hub((form, name, lang, json, front, locale, index, check) -> add().pushButtonViaForm(form), webForm,null,null,"null");
    }

    @Step("нажимаем на кнопку {webName}")
    default void pushButton(String webName, String webLang) {
        hub((form, name, lang, json, front, locale, index, check) -> add().pushButtonViaText(name), null, webName, webLang,"null");
    }
}
