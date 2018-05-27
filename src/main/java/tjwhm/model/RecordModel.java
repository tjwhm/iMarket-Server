package tjwhm.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tjwhm.model.bean.RecordBean;

public class RecordModel extends BaseModel {
    ClothesModel clothesModel = new ClothesModel();

    public List<RecordBean> getRecord(String after, String before, String name, String brand, double low, double high) throws SQLException {
        if (after == null || after.isEmpty()) after = "1970-01-01 00:00:00";
        if (before == null || before.isEmpty()) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        List<Integer> sids = clothesModel.getSID(name, brand);
        sids.add(-1);
        StringBuilder sidString = new StringBuilder("(");
        sids.forEach(it -> sidString.append(String.valueOf(it)).append(","));
        sidString.deleteCharAt(sidString.length() - 1);
        sidString.append(")");
        // TODO: 2018/5/21 add food's sids
        ResultSet resultSet = statement.executeQuery("select * from record where time > " +
                "\"" + after + "\" and time < \"" + before + "\" and price > " + String.valueOf(low) + " and price < " + String.valueOf(high) +
                " and sid in " + sidString + ";");

        return getRecordsFromResultSet(resultSet);
    }

    public List<RecordBean> getRecordForClothes(String after, String before, String name, String brand, double low, double high,
                                                String color, String size, String suitable_crowd) throws SQLException {
        if (after == null) after = "1970-01-01 00:00:00";
        if (before == null) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        if (color == null) color = "";
        if (size == null) size = "";
        if (suitable_crowd == null) suitable_crowd = "";

        List sids = clothesModel.getSID(name, brand, color, size, suitable_crowd);

        StringBuilder sidString = new StringBuilder("(");
        sids.stream().forEach(it -> sidString.append(String.valueOf(it)).append(","));
        sidString.deleteCharAt(sidString.length() - 1);
        sidString.append(")");

        ResultSet resultSet = statement.executeQuery("select * from record where time > " +
                "\"" + after + "\" and time < \"" + before + "\" and price > " + String.valueOf(low) + " and price < " + String.valueOf(high) +
                " and sid in " + sidString + ";");

        return getRecordsFromResultSet(resultSet);
    }

    public List<RecordBean> getRecordForFood(String after, String before, String name, String brand, double low, double high,
                                             String shelf_life_from, String shelf_life_to, String origin) {
        if (after == null) after = "1970-01-01 00:00:00";
        if (before == null) before = "2100-01-01 00:00:00";
        if (name == null) name = "";
        if (brand == null) brand = "";
        if (high == 0.0) high = 9999.0;
        if (shelf_life_from == null) shelf_life_from = "";
        if (shelf_life_to == null) shelf_life_to = "";
        if (origin == null) origin = "";

        return null;
    }

    public List<RecordBean> getAllRecords() throws SQLException {
        String sql = "select * from record";
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


}
