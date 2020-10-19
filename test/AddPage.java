package cms_tests.objects.pages.add;

import java.util.*;

import cms_tests.objects.Page;
import cms_tests.objects.pages.picture.PicturePage;
import cms_tests.objects.pages.search.SearchPage;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.JavascriptExecutor;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public abstract class AddPage extends Page {

    @Qualifier("abstractSearch")
    @Autowired
    protected SearchPage search;

    protected AddPage add() {
        return this;
    }
    protected abstract PicturePage picture();

    private SelenideElement formTitleAdd = $(byXpath("//strong[contains(text(),'Добавление : ')]"+onlyVisible));
    private SelenideElement formTitleFix = $(byXpath("//strong[contains(text(),'Редактирование : ')]"+onlyVisible));
    private SelenideElement buttonSave = $(byXpath("(//button[text()='Сохранить'])"+onlyVisible));
    private SelenideElement buttonApply = $(byXpath("(//button[text()='Применить'])"+onlyVisible));
    private SelenideElement buttonPublish = $(byXpath("(//button[text()='Опубликовать'])"+onlyVisible));

    @Step("проверяем текущую страницу на наличие {section}")
    public void checkCurrentPage(String section) {
        formTitleAdd.waitUntil(exist,8000).shouldBe(visible).shouldHave(text(section));
    }

    @Step("проверяем текущую страницу на наличие {section}")
    protected void checkCurrentPage(SelenideElement element, String section) {
        element.waitUntil(exist,8000).waitUntil(visible,8000).shouldHave(text(section));
    }

    @Step("заполняем поле")
    void inputValueViaForm(String webForm, String text, boolean check) {
        String path = "//input[@data-selenium='"+webForm+"']"+getAncestorsPath(depth)+onlyVisible;
        inputValue(getElement(path), text, check);
    }

    @Step("заполняем поле")
    void inputValueViaName(String webName, String webLang, String text, boolean check) {
        String sectionLang = emptyString;
        if (webLang != null) sectionLang = "following-sibling::*/descendant::*[text()='"+webLang+"']/../";
        String path = "//label[text()='"+webName+"']/../"+sectionLang+"following-sibling::*/descendant::*[@type='input']"+getAncestorsPath(depth)+onlyVisible;
        inputValue(getElement(path), text, check);
    }

    private void inputValue(SelenideElement element, String webText, boolean check) {
        element.hover().click();
        element.setValue(webText);
        if (check) element.waitUntil(value(webText),8000);
    }

    @Step("вводим текст в поле с указанным названием")
    void inputFieldViaName(String webName, String webLang, String text, boolean check){
        String sectionLang = emptyString;
        if (webLang != null) sectionLang = "[text()='"+webLang+"']";
        SelenideElement thisField = $(byXpath("//label[text()='"+webName+"']/../following-sibling::*/descendant::*"+sectionLang+"/../following-sibling::*/*/*/descendant::p"+getAncestorsPath(depth)+onlyVisible));

        thisField.hover().click();
        JavascriptExecutor jse = (JavascriptExecutor)getWebDriver(); // активируем лунную призму -->
        jse.executeScript("arguments[0].innerHTML = '"+text+"';", thisField.toWebElement()); // <-- получаем силу
        if (check) thisField.shouldHave(text(text));
    }

    @Step("вводим текст в поле с указанным названием")
    void inputAceViaName(String webName, String webLang, String text, boolean check) {
        String sectionLang = emptyString;
        if (webLang != null) sectionLang = "/../following-sibling::*/descendant::*[text()='"+webLang+"']";
        SelenideElement thisFieldClickArea = $x("//label[text()='"+webName+"']"+sectionLang+"/../following-sibling::*/descendant::*[@class='ace_content']"+getAncestorsPath(depth)+onlyVisible);
        SelenideElement thisFieldInput = $x("//label[text()='"+webName+"']"+sectionLang+"/../following-sibling::*/descendant::*[@class='ace_text-input']"+getAncestorsPath(depth)+onlyVisible);

        thisFieldClickArea.hover().click();
        thisFieldInput.setValue(text);
        if (check) thisFieldInput.shouldHave(value(text));
//        String guid = $(byAttribute("data-selenium","form.bannerContent.desktop.html.rus")).getAttribute("name");
//        String script = "appInterface.ACE_EDITORS['"+guid+"'].setValue('123456', -1)";
//        JavascriptExecutor jse_t = (JavascriptExecutor)getWebDriver(); // <--JShack
//        jse_t.executeScript(script);
    }

    @Step("проставляем значение в чекбокс")
    void setValueViaForm(String webForm, String value, boolean check) {
        String path = "//input[@data-selenium='"+webForm+"']"+getAncestorsPath(depth)+onlyVisible;
        changeBoxValue($(byXpath(path)), value, check);
    }

    @Step("проставляем значение в чекбокс")
    void setValueViaName(String webName, String webLang, String value, boolean check) {
        String sectionLang = (webLang != null) ? "following-sibling::*/descendant::*[text()='"+webLang+"']/" : emptyString;
        String path = "//label[text()='"+webName+"']/../"+sectionLang+"following-sibling::*/descendant::*[@type='checkbox']"+getAncestorsPath(depth)+onlyVisible;
        changeBoxValueJS($(byXpath(path)), value, check);
    }

    private void changeBoxValue(SelenideElement box, String value, boolean check) {
        boolean isSelected = !value.equals("false");
        box.waitUntil(exist, 8000).waitUntil(visible, 4000);
        box.setSelected(isSelected);
        if (check && !isSelected) box.shouldNotBe(selected);
        else if (check) box.waitUntil(selected,4000);
    }

    private void changeBoxValueJS(SelenideElement box, String value, boolean check) {
        boolean isSelected = !value.equals("false");
        box.waitUntil(exist, 8000);
        JavascriptExecutor jse = (JavascriptExecutor)getWebDriver();
        jse.executeScript("arguments[0].checked = 'value';", box.toWebElement());
        if (check && !isSelected) box.shouldNotBe(selected);
        else if (check) box.waitUntil(selected,4000);
    }

    @Step("проверем значение в чекбоксе")
    boolean checkValueViaForm(String webForm) {
        return $(byXpath("//input[@data-selenium='"+webForm+"']"+getAncestorsPath(depth)+onlyVisible)).isSelected();
    }

    @Step("проверем значение в чекбоксе")
    boolean checkValueViaName(String webName, String webLang) {
        String sectionLang = (webLang != null) ? "following-sibling::*/descendant::*[text()='"+webLang+"']/" : emptyString;
        String path = "//label[text()='"+webName+"']/../"+sectionLang+"following-sibling::*/descendant::*[@type='checkbox']"+getAncestorsPath(depth)+onlyVisible;
        return $(byXpath(path)).isSelected();
    }

    @Step("получаем значение выбранного атрибута")
    String getValueViaForm(String webText) {
        String lang = "ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";    // hack xpath 1.0 to be case insensitive
        return $(byXpath("//div[translate(text(),'"+lang+"','"+lang.toLowerCase()+"')='"+webText.toLowerCase()+"']/parent::*[contains(@data-selenium,'form.')]"+getAncestorsPath(depth)+onlyVisible)).getAttribute("data-selenium").replaceAll("\\w*\\.{1,2}","");
    }

    @Step("кликаем на значение {value} в форме {webForm}")
    protected void switchValueViaForm(String webForm, String value, boolean check) {
        $(byXpath("//button[@data-selenium='"+webForm+"."+value+"']"+getAncestorsPath(depth)+onlyVisible)).waitUntil(exist,15000).waitUntil(visible,15000).hover().click();
        if (check) $(byXpath("//input[@data-selenium='"+webForm+"']/following::input[1]"+getAncestorsPath(depth)+onlyVisible)).shouldHave(value(value));
    }

    @Step("нажимаем на поле {webForm}")
    protected void pushInputViaForm(String webForm, boolean check) {
        $(byXpath("//input[@data-selenium='"+webForm+"']"+getAncestorsPath(depth)+onlyVisible)).waitUntil(exist,8000).waitUntil(visible,8000).hover().click();
        if (check) $(byXpath("//div[@class='card SelectAutocompleteItems'][@style='display: block;']"+getAncestorsPath(depth)+onlyVisible)).waitUntil(exist,8000).waitUntil(visible,8000);
    }

    @Step("Нажимаем на кнопку")
    void pushButtonViaForm(String webForm) {
        $(byXpath("//button[@data-selenium='"+webForm+"']"+getAncestorsPath(depth)+onlyVisible)).hover().click();
    }

    @Step("Нажимаем на кнопку")
    void pushButtonViaText(String text) {
        SelenideElement button = $(byXpath("//button[contains(text(),'"+text+"')]"+getAncestorsPath(depth)+onlyVisible));
        button.waitUntil(exist,8000).scrollIntoView(false);
        button.hover().click();
    }

    @Step("выбираем фронт")
    void openFrontViaForm(String front, boolean check) {
        SelenideElement buttonFront = $(byXpath("//*[@data-selenium='form.blockType."+front+"']"+getAncestorsPath(depth)+onlyVisible));
        buttonFront.scrollIntoView(false);
        buttonFront.hover().click();
        if (check) assertTrue(getFrontStatusViaForm(front));
    }
    @Step("получаем статус указанного фронта")
    boolean getFrontStatusViaForm(String front) {
        SelenideElement buttonFront = $(byXpath("//*[@data-selenium='form.blockType."+front+"']"+getAncestorsPath(depth)+onlyVisible));
        return buttonFront.getAttribute("class").contains("active");
    }
    @Step("получаем название указанного фронта")
    String getActiveFrontName() {
        return $(byXpath("//button[contains(@data-selenium,'form.blockType.')][contains(@class,'active')]")).getText().toLowerCase();
    }

    @Step("запоминаем id записи")
    public String getId() {
        return formTitleFix.text().replaceAll("\\D","");
    }

    @Step("нажимаем добавить картинку")
    public void addImageViaName(String webName, String webLang, boolean check) {
        String language = emptyString;
        if (webLang != null) language = "/../following-sibling::*/descendant::label[text()='"+webLang+"']";
        SelenideElement buttonAddImage = $$(byXpath("//label[text()='"+webName+"']"+language+"/../following-sibling::*/descendant::*[contains(@data-selenium,'addImage')]"+getAncestorsPath(depth))).findBy(visible);
        buttonAddImage.scrollIntoView(false);
        buttonAddImage.hover().click();
        if (check) picture().checkCurrentPage();
    }

    @Step("проверяем добавленную картинку")
    public void checkImageViaName(String webName, String webLang, String value, boolean check) {
        String language = emptyString;
        if (webLang != null) language = "/../following-sibling::*/descendant::label[text()='"+webLang+"']";
        SelenideElement objectPreviewPicture = $(byXpath("//label[text()='"+webName+"']"+getAncestorsPath(depth)+onlyVisible+language+"/../following-sibling::*/descendant::*[@class='img']"));
        if (check) objectPreviewPicture.should(exist).shouldBe(visible);
        if (check) Assertions.assertTrue(objectPreviewPicture.getAttribute("style").contains(value));
    }

    @Step("проверяем наличие сообщения <{message}> в объекте {webText}")
    void checkAlertAction(List<String> webText, String message, List<String> fronts) {
        String alert = "*[contains(text(),'"+message+"')]";
        StringBuilder path = new StringBuilder("//");
        if (webText != null) for (String section : webText) path.append("label[text()='"+section+"']/../../descendant::");
        $(byXpath(path+alert+onlyVisible)).waitUntil(exist,4000).shouldBe(visible);
        if (fronts != null) for (String front : fronts) $(byXpath(path+alert+onlyVisible)).shouldHave(text(front));
    }

    @Step("нажимаем применить")
    void pushApply(boolean success, boolean check) {
        long timer = 25000;
        SelenideElement element;
        if (success) element = formTitleFix;
        else element = formTitleAdd;
        buttonApply.hover().click();
        if (check) element.waitUntil(exist, timer).waitUntil(visible, timer);
        if (check && success) {
            element.shouldHave(text("ID"));
            buttonPublish.should(exist).shouldBe(visible);
        }
    }
    @Step("нажимаем сохранить, проверяем выход на форму поиска")
    public void pushSave(String section, boolean check) {
        String label = emptyString;
        if (section != null) label = "//strong[contains(text(),'"+section+"')]/../following-sibling::div/descendant::";
        buttonSave = $x(label+"button[text()='Сохранить']");
        buttonSave.hover().click();
        if (check) {
            formTitleFix.shouldHave(exactText("Редактирование : "+section));
            $(byText("Список : " + section)).waitUntil(exist,4000).shouldBe(visible);
            search.checkCurrentPage(section);
        }
    }
    @Step("нажимаем опубликовать")
    void pushPublish() {
        buttonPublish.waitUntil(exist,8000).waitUntil(visible,8000).hover().click();
    }
}
