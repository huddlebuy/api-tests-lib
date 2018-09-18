package com.perkbox.util;

import com.perkbox.testbase.Requests;
import com.perkbox.util.reporting.Report;
import com.perkbox.util.reporting.ReportEntity;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SuiteListener implements ISuiteListener {

    private static final boolean DISABLED_HEALTH_CHECK = Config.get("HEALTH_CHECK") != null && Config.get("HEALTH_CHECK").equals("disabled");
    private static final boolean ENABLED_REPORTING = Config.get("TEST_REPORTING") != null && Config.get("TEST_REPORTING").equals("enabled");
    private static final String[] COLUMNS = {"Endpoint", "Method", "Test Data", "Description", "Status"};
    private static ArrayList<ReportEntity> report = new ArrayList<>();

    @AfterMethod
    public void addToReport(ITestResult result) {
        if (ENABLED_REPORTING) {
            String status;

            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    status = "PASSED";
                    break;
                case ITestResult.FAILURE:
                    status = "FAILED";
                    break;
                default:
                    status = "INCOMPLETE";
            }

            String thePackage = result.getTestClass().getRealClass().getPackage().getName().split(Pattern.quote("."))[3];
            String packageAsUpper = thePackage.substring(0, 1).toUpperCase() + thePackage.substring(1).toLowerCase();

            Method method = result.getMethod().getConstructorOrMethod().getMethod();
            Report annotation = method.getAnnotation(Report.class);
            String description = annotation != null ? annotation.description() : null;

            report.add(new ReportEntity(
                    packageAsUpper,
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    result.getParameters(),
                    description,
                    status
            ));
        }
    }

    @AfterSuite
    public void writeReport() {
        if (ENABLED_REPORTING) {
            try {
                String file = System.getProperty("user.dir") + "/data/output/report.xlsx";

                report.sort(Comparator.comparing(ReportEntity::getPackageName));

                Map<String, ArrayList<ReportEntity>> map = new HashMap<>();
                ArrayList<ReportEntity> list = new ArrayList<>();

                for (int i = 0; i < report.size(); i++) {
                    ReportEntity curr = report.get(i);

                    if (i == 0 || curr.packageName.equals(report.get(i - 1).packageName)) {
                        list.add(curr);

                        if (i == report.size() - 1) {
                            map.put(curr.packageName, list);
                        }
                    } else {
                        map.put(report.get(i - 1).packageName, list);

                        list = new ArrayList<>();
                        list.add(curr);
                    }
                }

                Workbook workbook = new XSSFWorkbook();

                // Sheet loop
                for (Map.Entry<String, ArrayList<ReportEntity>> entry : map.entrySet()) {
                    createSheet(workbook, entry.getKey(), entry.getValue());
                }

                // Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);

                fileOut.close();
                workbook.close();
            } catch (Exception e) {
                System.out.println("ERROR:: Could not complete reporting.");
            }
        }
    }

    public void onStart(ISuite iSuite) {
        if (!DISABLED_HEALTH_CHECK) {
            int serviceReady = 0;
            ExtractableResponse<Response> response = callHealth();

            String marker = "#########################################################";
            System.out.println(marker + "\nWaiting for service to be up ...");

            while (serviceReady < 20 && response.statusCode() != 200) {
                try {
                    Thread.sleep(1000);
                    serviceReady++;
                    response = callHealth();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (response.statusCode() != 200) {
                System.out.println("Exiting... Service health check was unsuccessful.\n" + marker);
                System.exit(-1);
            }

            System.out.println("Service ready!\nSeconds delayed to be ready: " + serviceReady + "\n" + marker);
        }
    }

    public void onFinish(ISuite suite) {}

    private ExtractableResponse<Response> callHealth() {
        return new Requests(Env.get("HEALTH_URL")).get().getResponse();
    }

    private void createSheet(Workbook workbook, String sheetName, ArrayList<ReportEntity> report) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.LIGHT_BLUE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for (int i = 0; i < COLUMNS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMNS[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Other rows and cells with report data
        int index = 0, rowNum = 1;
        for (ReportEntity entity : report) {
            if (index != 0 && !report.get(index).endpoint.equals(report.get(index - 1).endpoint)) {
                sheet.createRow(rowNum++); // add row between endpoint test groups
            }
            index++;

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(entity.endpoint);
            row.createCell(1).setCellValue(entity.method);
            row.createCell(2).setCellValue(entity.testData);
            row.createCell(3).setCellValue(entity.description);
            row.createCell(4).setCellValue(entity.status);
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < COLUMNS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}