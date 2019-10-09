
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Main{
    public static void main(String[] args){
        String fileName = FilenameUtils.separatorsToSystem(args[0]);
        String propertyName=FilenameUtils.separatorsToSystem(args[1]);
        System.setProperty("webdriver.chrome.driver", propertyName);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(fileName)) {
            JSONObject obj = (JSONObject) parser.parse(reader);
            JSONArray row = (JSONArray) obj.get("actions");
            Iterator<JSONObject> actionsIterator = row.iterator();
            while (actionsIterator.hasNext()){
                JSONObject action = actionsIterator.next();
                String name = (String) action.get("name");
                JSONArray parameters = (JSONArray) action.get("parameters");
                ArrayList<String> params = new ArrayList<>(parameters);
                switch (name){
                    case "openURL":{
                        driver.get(params.get(0));
                        break;}
                    case "Click":{
                        WebElement elem = driver.findElement(By.xpath(params.get(0)));
                        elem.click();
                        break;
                    }
                    case "setValue":{
                        WebElement elem = driver.findElement(By.xpath(params.get(0)));
                        elem.sendKeys(params.get(1));
                        break;
                    }
                    case "Screenshot":{
                        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                        FileUtils.copyFile(screenshot, new File(FilenameUtils.getFullPath(fileName)+"screenshot.png"));
                        break;
                    }
                    case "checkElementVisible":{
                        boolean isVisible = !driver.findElements(By.xpath(params.get(0))).isEmpty();
                        System.out.print(isVisible);
                        break;
                    }

                    default:{
                        break;
                    }
                }

            }
        } catch (Exception ex){
            if (ex instanceof  IOException) System.out.println("File not found.");
            if (ex instanceof ParseException) System.out.println("Parsing wasn't successful");
        }
        driver.close();
        System.exit(0);
    }
}
