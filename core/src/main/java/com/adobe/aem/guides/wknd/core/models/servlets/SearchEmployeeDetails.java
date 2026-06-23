package com.adobe.aem.guides.wknd.core.models.servlets;


import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.day.cq.dam.api.Asset;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/exportexcelfortext",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET"
        }
)
public class SearchEmployeeDetails extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            // Get the Excel file path in DAM
            String excelFilePath = "/content/dam/wknd-shared/en/magazine/arctic-surfing/samplefile.xlsx";

            // Get the resource resolver
            ResourceResolver resourceResolver = request.getResourceResolver();
            String shortCode = request.getParameter("shortCode");

            // Check if the Excel file exists
            Resource excelResource = resourceResolver.getResource(excelFilePath);
            if (excelResource == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Adapt the resource to an Asset
            Asset asset = Objects.requireNonNull(excelResource).adaptTo(Asset.class);
            if (asset == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // Get an InputStream from the Asset's original
            InputStream excelInputStream = Objects.requireNonNull(asset).getOriginal().getStream();

            // Process the Excel content and convert it to JSON
            Workbook workbook = new XSSFWorkbook(excelInputStream);
            List<Map<String, String>> jsonData = new ArrayList<>();

            // Iterate through the Excel rows and columns
            Sheet sheet = workbook.getSheetAt(0); // Assuming it's the first sheet
            Row headerRow = sheet.getRow(0); // Assuming the first row is the header row
            int totalColumns = headerRow.getLastCellNum();

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row dataRow = sheet.getRow(i);
                Map<String, String> rowMap = new LinkedHashMap<>();

                for (int j = 0; j < totalColumns; j++) {
                    Cell headerCell = headerRow.getCell(j);
                    Cell dataCell = dataRow.getCell(j);

                    if (headerCell != null && dataCell != null) {
                        // Check the cell type to handle numeric and string values

                        if (dataCell.getCellType() == CellType.STRING) {
                            rowMap.put(headerCell.getStringCellValue(), dataCell.getStringCellValue());
                        } else if (dataCell.getCellType() == CellType.NUMERIC) {
                            // Handle numeric values as integers (remove decimals)
                            int age = (int) dataCell.getNumericCellValue();
                            rowMap.put(headerCell.getStringCellValue(), String.valueOf(age));
                        }
                    }
                }

                jsonData.add(rowMap);
            }

            // Prepare the JSON response
            List<Map<String, String>> peopleList = new ArrayList<>();
            for (Map<String, String> personData : jsonData) {
                String personShortCode = personData.get("Short Code");
                if(StringUtils.isNotEmpty(shortCode) && personShortCode.equals(shortCode)){
                    Map<String, String> person = new LinkedHashMap<>();
                    person.put("threePayShortCode", personData.get("ThreePay/Short Code"));
                    person.put("ShortCode", personData.get("Short Code"));
                    person.put("Merchant", personData.get("Merchant"));
                    person.put("ServiceCategory", personData.get("Service Category/Type of Service"));
                    person.put("OutgoingMessageCost", personData.get("Outgoing Message Cost"));
                    person.put("IncomingMessageCost", personData.get("Incoming Message Cost"));
                    person.put("MerchantNumber", personData.get("Merchant Number"));
                    person.put("MerchantContact", personData.get("Merchant Contact"));
                    person.put("MerchantWebsite", personData.get("Merchant Website"));
                    person.put("PaymentIntermediary", personData.get("Payment Intermediary"));
                    person.put("PaymentIntermediaryNumber", personData.get("PaymentIntermediaryNumber"));
                    person.put("PaymentIntermediaryContact", personData.get("Payment Intermediary Contact"));
                    person.put("PaymentIntermediaryWebsite", personData.get("Payment Intermediary Website"));
                    peopleList.add(person);
                }
            }

            Map<String, List<Map<String, String>>> jsonResponse = new LinkedHashMap<>();
            jsonResponse.put("Combined Tool Data", peopleList);

            // Set response content type and write JSON content to the response
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(jsonResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

