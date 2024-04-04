import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class openWebsite {
    private static WebDriver driver;
    private JavascriptExecutor jsExecutor;
    private HomePage homePage;
    private ResultsPage resultsPage;
    private PaymentPage paymentPage;

    @BeforeTest
    public void setup() {
        String chromePath = System.getProperty("user.dir") +"\\src\\main\\resources\\chromedriver.exe";

        System.setProperty("webdriver.chrome.driver", chromePath);
        driver = new ChromeDriver();
        driver.navigate().to("https://ksrtc.in/oprs-web/guest/home.do?h=1");
        driver.manage().window().setSize(new Dimension(1024,768));

        homePage = new HomePage(driver);
        resultsPage = new ResultsPage(driver);
        paymentPage = new PaymentPage(driver);

        jsExecutor = (JavascriptExecutor) driver;

       // homePage.openURL(TestConfig.BASE_URL);
    }

    @Test
    public void testChooseRoutes(){
        homePage.clickNextRouteBtn();
        homePage.clickSpecificRouteBtn();
    }

    @Test (dependsOnMethods = "testChooseRoutes")
    public void testChooseDepartureDate() {
       // jsExecutor.executeScript("arguments[0].click();", homePage.getDepartureDatePickerNextBtn());
        jsExecutor.executeScript("arguments[0].click();", homePage.getDepartureDatePickerDayBtn());
    }

    @Test (dependsOnMethods = "testChooseRoutes")
    public void testSearchForBus() {
        homePage.clickSearchForBusBtn();
    }

    @Test (dependsOnMethods = "testSearchForBus")
    public void testSelectSeat() {
        resultsPage.clickSelectSeatBtn();
    }

    @Test (dependsOnMethods = "testSelectSeat")
    public void testSelectAvailableSeat() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
       resultsPage.clickAvailableSeatBtn();
       resultsPage.clickAvailableSeatBtn();
    }

    @Test (dependsOnMethods = "testSelectAvailableSeat")
    public void testSelectBoardingDroppingPoints() {
        resultsPage.clickBoardingPointSelect();
        resultsPage.clickDroppingPointSelect();
    }

    @Test (dependsOnMethods = "testSelectBoardingDroppingPoints")
    public void testFillCustomerDetails() {
        resultsPage.setMobileNoTxtFld("6789125987");
        resultsPage.setEmailTxtFld("test@test.com");
    }

   @DataProvider(name = "passengerData")
   public Object[][] getPassengerData() {
       return Helpers.getPassengerData();
   }

    @Test (dataProvider = "passengerData", dependsOnMethods = "testSelectBoardingDroppingPoints")
    public void testFillPassengerDetails(String name, String age, String gender, String concession, String country, int passengerIndex) {
        resultsPage.setNameTxtFld(name,passengerIndex);
        resultsPage.setAgeTxtFld(age,passengerIndex);

        Helpers.selectOptionInCustomDropdown(resultsPage.getGenderDropDown(passengerIndex), gender);
        Helpers.selectOptionInCustomDropdown(resultsPage.getConcessionDropDown(passengerIndex), concession);
        Helpers.selectOptionInCustomDropdown(resultsPage.getCountryDropDown(passengerIndex), country);
    }

    @Test (dependsOnMethods = "testFillPassengerDetails")
    public void testClickMakePayment() {
        driver.findElement(By.xpath("//input[@id='termsChk']")).click();
         //resultsPage.clickMakeAgreeBtn();
         resultsPage.clickMakePaymentBtn();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test (dependsOnMethods = "testClickMakePayment")
    public void testAddPaymentData() {
        driver.switchTo().frame(paymentPage.getIframe());
        paymentPage.clickPaymentMethodBtn();
        paymentPage.setCardNoTxtFld("378282246310005");
        paymentPage.setCardExpiryTxtFld("03/26");
        paymentPage.setCardCvvTxtFld("1234");
    }

//    @AfterTest
//    public void teardown() {
//        driver.quit();
//    }
}