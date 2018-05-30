package tjwhm.model;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tjwhm.model.bean.RecordBean;

public class RecordModel extends BaseModel {
    private ClothesModel clothesModel = new ClothesModel();
    private FoodModel foodModel = new FoodModel();

    public List<RecordBean> getRecord(String after, String before, String name, String brand, double low, double high) throws SQLException {
        if (after == null || after.isEmpty()) after = "1970-01-01 00:00:00";
        if (before == null || before.isEmpty()) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        List<Integer> sids = clothesModel.getSID(name, brand);
        sids.addAll(foodModel.getSID(name, brand));
        sids.add(-1);
        StringBuilder sidString = new StringBuilder("(");
        sids.forEach(it -> sidString.append(String.valueOf(it)).append(","));
        sidString.deleteCharAt(sidString.length() - 1);
        sidString.append(")");
        ResultSet resultSet = statement.executeQuery("select * from record where time > " +
                "\"" + after + "\" and time < \"" + before + "\" and price > " + String.valueOf(low) + " and price < " + String.valueOf(high) +
                " and sid in " + sidString + " order by time desc;");

        return getRecordsFromResultSet(resultSet);
    }

    public List<RecordBean> getRecordForClothes(String after, String before, String name, String brand, double low, double high,
                                                String color, String size, String suitable_crowd) throws SQLException {
        if (after == null || after.isEmpty()) after = "1970-01-01 00:00:00";
        if (before == null || before.isEmpty()) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        if (color == null) color = "";
        if (size == null) size = "";
        if (suitable_crowd == null) suitable_crowd = "";

        List<Integer> sids = clothesModel.getSID(name, brand, color, size, suitable_crowd);
        sids.add(-1);
        StringBuilder sidString = new StringBuilder("(");
        sids.forEach(it -> sidString.append(String.valueOf(it)).append(","));
        sidString.deleteCharAt(sidString.length() - 1);
        sidString.append(")");

        ResultSet resultSet = statement.executeQuery("select * from record where time > " +
                "\"" + after + "\" and time < \"" + before + "\" and price > " + String.valueOf(low) + " and price < " + String.valueOf(high) +
                " and sid in " + sidString + " order by time desc;");

        return getRecordsFromResultSet(resultSet);
    }

    public List<RecordBean> getRecordForFood(String after, String before, String name, String brand, double low, double high,
                                             String shelf_life_from, String shelf_life_to, String origin) throws SQLException {
        if (after == null || after.isEmpty()) after = "1970-01-01 00:00:00";
        if (before == null || before.isEmpty()) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        if (shelf_life_from == null || shelf_life_from.isEmpty())
            shelf_life_from = "1970-01-01 00:00:00";
        if (shelf_life_to == null || shelf_life_to.isEmpty()) shelf_life_to = "2100-01-01 00:00:00";
        if (origin == null) origin = "";

        List<Integer> sids = foodModel.getSID(name, brand, shelf_life_from, shelf_life_to, origin, low, high);
        sids.add(-1);
        StringBuilder sidString = new StringBuilder("(");
        sids.forEach(it -> sidString.append(String.valueOf(it)).append(","));
        sidString.deleteCharAt(sidString.length() - 1);
        sidString.append(")");
        String select = "select * from record where sid in " + sidString
                + " and time > \"" + after + "\" and time <\"" + before + "\"  order by time desc;";
        ResultSet resultSet = statement.executeQuery(select);
        List<RecordBean> list = new ArrayList<>();
        while (resultSet.next()) {
            RecordBean recordBean = new RecordBean();
            recordBean.time = resultSet.getString("time");
            recordBean.sid = resultSet.getInt("sid");
            recordBean.num = resultSet.getInt("num");
            recordBean.price = resultSet.getDouble("price");
            recordBean.value = resultSet.getDouble("value");
            recordBean.type = resultSet.getString("type");
            list.add(recordBean);
        }
        return list;
    }

    public List<RecordBean> getAllRecords() throws SQLException {
        String sql = "select * from record order by time desc;";
        ResultSet resultSet = statement.executeQuery(sql);
        List<RecordBean> list = new ArrayList<>();
        while (resultSet.next()) {
            RecordBean temp = new RecordBean();
            temp.sid = resultSet.getInt("sid");
            temp.time = resultSet.getTimestamp("time").toString();
            temp.num = resultSet.getInt("num");
            temp.price = resultSet.getDouble("price");
            temp.value = resultSet.getDouble("value");
            temp.type = resultSet.getString("type");
            list.add(temp);
        }
        return list;
    }

    private List<RecordBean> getRecordsFromResultSet(ResultSet resultSet) throws SQLException {
        List<RecordBean> records = new ArrayList<>();
        while (resultSet.next()) {
            RecordBean recordBean = new RecordBean();
            recordBean.time = resultSet.getTimestamp("time").toString();
            recordBean.sid = resultSet.getInt("sid");
            recordBean.num = resultSet.getInt("num");
            recordBean.price = resultSet.getDouble("price");
            recordBean.value = resultSet.getDouble("value");
            recordBean.type = resultSet.getString("type");
            records.add(recordBean);
        }
        return records;
    }


    public Boolean exportPurchaseRecords(String from, String to) throws SQLException, IOException {
        String select = "select sid,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"purchase\" and time > \"" + from + "\" and time < \"" + to + "\" group by sid;";
        ResultSet resultSet = statement.executeQuery(select);


        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("sid");
        row.createCell(1).setCellValue("sum_num");
        row.createCell(2).setCellValue("sum_value");
        int rowIndex = 1;
        generateRow(resultSet, sheet, rowIndex);
        FileOutputStream outputStream = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/purchase-sid "
                + getStringWithFromTo(from, to) + ".xls");
        workbook.write(outputStream);
        outputStream.flush();

        String select1 = "select brand,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"purchase\" and time > \"" + from + "\" and time < \"" + to + "\" group by brand;";
        ResultSet set = statement.executeQuery(select1);
        HSSFWorkbook wb1 = new HSSFWorkbook();
        HSSFSheet sheet1 = wb1.createSheet("sheet0");
        HSSFRow row1 = sheet1.createRow(0);
        row1.createCell(0).setCellValue("brand");
        row1.createCell(1).setCellValue("sum_num");
        row1.createCell(2).setCellValue("sum_value");
        int rowI = 1;
        getRows(set, sheet1, rowI);
        FileOutputStream outputStream1 = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/purchase-brand "
                + getStringWithFromTo(from, to) + ".xls");
        wb1.write(outputStream1);
        outputStream1.flush();
        return true;
    }

    private void getRows(ResultSet set, HSSFSheet sheet1, int rowI) throws SQLException {
        HSSFRow row1;
        while (set.next()) {
            row1 = sheet1.createRow(rowI);
            row1.createCell(0).setCellValue(set.getString("brand"));
            row1.createCell(1).setCellValue(set.getInt("sum_num"));
            row1.createCell(2).setCellValue(set.getDouble("sum_value"));
            ++rowI;
        }
    }

    public Boolean exportSellRecords(String from, String to) throws SQLException, IOException {
        String select = "select sid,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"sell\" and time > \"" + from + "\" and time < \"" + to + "\" group by sid;";
        ResultSet resultSet = statement.executeQuery(select);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("sid");
        row.createCell(1).setCellValue("sum_num");
        row.createCell(2).setCellValue("sum_value");
        int rowIndex = 1;
        generateRow(resultSet, sheet, rowIndex);
        FileOutputStream outputStream = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/sell-sid "
                + getStringWithFromTo(from, to) + ".xls");
        workbook.write(outputStream);
        outputStream.flush();


        String select1 = "select brand,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"sell\" and time > \"" + from + "\" and time < \"" + to + "\" group by brand;";
        ResultSet res = statement.executeQuery(select1);
        HSSFWorkbook wb1 = new HSSFWorkbook();
        HSSFSheet sheet1 = wb1.createSheet("sheet0");
        HSSFRow row1 = sheet1.createRow(0);
        row1.createCell(0).setCellValue("brand");
        row1.createCell(1).setCellValue("sum_num");
        row1.createCell(2).setCellValue("sum_value");
        int rowI = 1;
        getRows(res, sheet1, rowI);
        FileOutputStream outputStream1 = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/sell-brand "
                + getStringWithFromTo(from, to) + ".xls");
        wb1.write(outputStream1);
        outputStream1.flush();

        return true;
    }

    private void generateRow(ResultSet resultSet, HSSFSheet sheet, int rowIndex) throws SQLException {
        HSSFRow row;
        while (resultSet.next()) {
            row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(resultSet.getInt("sid"));
            row.createCell(1).setCellValue(resultSet.getInt("sum_num"));
            row.createCell(2).setCellValue(resultSet.getDouble("sum_value"));
            ++rowIndex;
        }
    }

    public Boolean exportAllRecords(String from, String to) throws SQLException, IOException {
        String select = "select sid,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"sell\" and time > \"" + from + "\" and time < \"" + to + "\" group by sid;";
        ResultSet resultSet = statement.executeQuery(select);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("sid");
        row.createCell(1).setCellValue("sum_num");
        row.createCell(2).setCellValue("sum_value");
        row.createCell(3).setCellValue("type");
        int rowIndex = 1;
        while (resultSet.next()) {
            row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(resultSet.getInt("sid"));
            row.createCell(1).setCellValue(resultSet.getInt("sum_num"));
            row.createCell(2).setCellValue(resultSet.getDouble("sum_value"));
            row.createCell(3).setCellValue("sell");
            ++rowIndex;
        }

        String select2 = "select sid,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"purchase\" and time > \"" + from + "\" and time < \"" + to + "\" group by sid;";

        ResultSet purSet = statement.executeQuery(select2);
        while (purSet.next()) {
            row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(purSet.getInt("sid"));
            row.createCell(1).setCellValue(purSet.getInt("sum_num"));
            row.createCell(2).setCellValue(purSet.getDouble("sum_value"));
            row.createCell(3).setCellValue("purchase");
            ++rowIndex;
        }
        FileOutputStream outputStream = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/sid"
                + getStringWithFromTo(from, to) + ".xls");
        workbook.write(outputStream);
        outputStream.flush();

        String selectB = "select brand,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"sell\" and time > \"" + from + "\" and time < \"" + to + "\" group by brand;";

        ResultSet bSet = statement.executeQuery(selectB);
        HSSFWorkbook workbook1 = new HSSFWorkbook();
        HSSFSheet sheet1 = workbook1.createSheet("sheet0");
        HSSFRow row1 = sheet1.createRow(0);
        row1.createCell(0).setCellValue("brand");
        row1.createCell(1).setCellValue("sum_num");
        row1.createCell(2).setCellValue("sum_value");
        row1.createCell(3).setCellValue("type");
        int rowIndex1 = 1;
        while (bSet.next()) {
            row1 = sheet1.createRow(rowIndex1);
            row1.createCell(0).setCellValue(bSet.getString("brand"));
            row1.createCell(1).setCellValue(bSet.getInt("sum_num"));
            row1.createCell(2).setCellValue(bSet.getDouble("sum_value"));
            row1.createCell(3).setCellValue("sell");
            ++rowIndex1;
        }

        String selectB2 = "select brand,sum(num) as sum_num,sum(value) as sum_value from record " +
                "where type = \"purchase\" and time > \"" + from + "\" and time < \"" + to + "\" group by brand;";
        ResultSet purSetB = statement.executeQuery(selectB2);
        while (purSetB.next()) {
            row1 = sheet1.createRow(rowIndex1);
            row1.createCell(0).setCellValue(purSetB.getString("brand"));
            row1.createCell(1).setCellValue(purSetB.getInt("sum_num"));
            row1.createCell(2).setCellValue(purSetB.getDouble("sum_value"));
            row1.createCell(3).setCellValue("purchase");
            ++rowIndex1;
        }
        FileOutputStream outputStream1 = new FileOutputStream("/Users/wang/Desktop/iMarket-Records/brand "
                + getStringWithFromTo(from, to) + ".xls");
        workbook1.write(outputStream1);
        outputStream1.flush();
        return true;

    }

    private String getStringWithFromTo(String from, String to) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < from.length(); i++) {
            if (from.charAt(i) != ' ' && from.charAt(i) != ':' && from.charAt(i) != '-') {
                res.append(from.charAt(i));
            }
        }

        res.append("-");
        for (int i = 0; i < to.length(); i++) {
            if (to.charAt(i) != ' ' && to.charAt(i) != ':' && to.charAt(i) != '-') {
                res.append(to.charAt(i));
            }
        }

        return res.toString();
    }
}
