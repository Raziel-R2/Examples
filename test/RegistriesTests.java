package cms_tests.tests;

import cms_tests.tests.core.Base;
import cms_tests.tests.core.Parameters;
import cms_tests.objects.data.Swagger;
import cms_tests.objects.forms.registries.*;
import cms_tests.objects.data.system.Reconfiguration;
import cms_tests.objects.data.parametrization.SettingsRegistries;
import cms_tests.tests.configuration.registries.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static cms_tests.objects.data.Messenger.message;
import static cms_tests.objects.data.Swagger.Purpose.BASE_CHECK;

@DisplayName("Registry")
public class RegistriesTests extends Base<RegistriesTests> implements Core, Parameters<RegistriesTests> {

    @Qualifier("registriesAdd")
    @Autowired
    private RegistriesAdd add;
    @Qualifier("registriesSearch")
    @Autowired
    private RegistriesSearch search;

    @Override
    public RegistriesTests test() {
        return this;
    }
    @Override
    public RegistriesAdd add() {
        return add;
    }
    @Override
    public RegistriesSearch search() {
        return search;
    }


    @Nested
    @Tag("smoke")
    @DisplayName("создание, удаление реестра")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class registryLifeCycleTest {
        @BeforeAll  //must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS)
        void configurationSetUp() {
            test().beforeEach();
            test().configuration();
            main.pushLogOut(true);
        }
        @BeforeEach
        void moveFromMain() {
            main.openCurrentSection(mapAPI.get(BASE_CHECK).getModule().getName(),true);
        }
        @Test
        @Order(1)
        @DisplayName("1.создание реестра, проверка")
        void nestedAddPage() {
            search.pushAddElement(mapAPI.get(BASE_CHECK).getModule().getName(),true);
            test().creation(null, null, null);
            add.confirmCreation(false);
            test().assertionAdvancedLogic(mapAPI.get(BASE_CHECK).checkExistence());
        }
        @Test
        @Order(2)
        @DisplayName("2.удаление реестра, проверка")
        void nestedDeletePage() {
            search.selectEntity(null);
            search.deleteEntity(memory.getId(),true);
            test().assertionAdvancedLogic(mapAPI.get(BASE_CHECK).checkAbsence());
        }
        @AfterEach
        void moveToMain() {
            main.pushWorkSpace(true);
        }
    }

    @Tag("factory")
    @Tag("registries")
    @MethodSource("factoryProvider")
    @ParameterizedTest(name = "{0}, signature:{1}")
    @DisplayName("реестр с указанными параметрами, создание/удаление")
    void factoryRegistries(String name, boolean isPositive, Boolean doExist, List<String> fronts, List<SettingsRegistries> settings, List<Reconfiguration> reconfiguration) {
        // create action
        message.introduceTest(name);
        test().configuration();
        Swagger currentObject = mapAPI.get(BASE_CHECK);
        String  sectionName   = mapAPI.get(BASE_CHECK).getModule().getName();
        main.openCurrentSection(sectionName, true);
        search.pushAddElement(sectionName,   true);
        test().creation(settings, reconfiguration, fronts);
        if (!isPositive) {
            add.failCreation(true);
            return;
        }
        add.confirmCreation(false);
        if (doExist) test().assertionAdvancedLogic(currentObject.checkExistence());
        else         test().assertionAdvancedLogic(currentObject.checkAbsence());
        // delete action
        main.openCurrentSection(sectionName, true);
        search.selectEntity(null);
        search.deleteEntity(memory.getId(), true);
        test().assertionAdvancedLogic(currentObject.checkAbsence());
    }
}