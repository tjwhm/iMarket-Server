package tjwhm.model;

// Created by tjwhm at 2018/5/27 10:07 PM

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tjwhm.model.bean.FoodBean;

public class FoodModel extends BaseModel {

    public List<FoodBean> getOnSaleFoodList() throws SQLException {
        String sql = "select * from food where on_sale is true;";
        ResultSet resultSet = statement.executeQuery(sql);
        List<FoodBean> list = new ArrayList<>();
        while (resultSet.next()) {
            FoodBean foodBean = new FoodBean();
            foodBean.sid = resultSet.getInt("sid");
            foodBean.name = resultSet.getString("name");
            foodBean.brand = resultSet.getString("brand");
            foodBean.shelf_life = resultSet.getString("shelf_life");
            foodBean.origin = resultSet.getString("origin");
            foodBean.price = resultSet.getDouble("price");
            foodBean.in_stock = resultSet.getInt("in_stock");
            foodBean.price1 = resultSet.getDouble("price1");
            foodBean.on_sale = resultSet.getBoolean("on_sale");
            list.add(foodBean);
        }
        return list;
    }

    public FoodBean getFoodInfo(String sid) throws SQLException {
        String sql = "select * from food where sid = " + sid + ";";
        ResultSet resultSet = statement.executeQuery(sql);
        FoodBean foodBean = new FoodBean();
        while (resultSet.next()) {
            foodBean.sid = resultSet.getInt("sid");
            foodBean.name = resultSet.getString("name");
            foodBean.brand = resultSet.getString("brand");
            foodBean.shelf_life = resultSet.getString("shelf_life");
            foodBean.origin = resultSet.getString("origin");
            foodBean.price = resultSet.getDouble("price");
            foodBean.in_stock = resultSet.getInt("in_stock");
            foodBean.price1 = resultSet.getDouble("price1");
            foodBean.on_sale = resultSet.getBoolean("on_sale");
        }
        return foodBean;
    }

    public boolean addFood(String name, String brand, String shelf_life, String origin,
                           double price, int in_stock, double price1) throws SQLException {
        String sql = "select count(sid) as count from food;";
        ResultSet resCount = statement.executeQuery(sql);
        int count = -1;
        while (resCount.next()) {
            count = resCount.getInt("count");
        }
        String insert = "insert into food values (" + String.valueOf(1000 + count) +
                ", \"" + name + "\", \"" + brand + "\", \"" + shelf_life + "\", \"" + origin +
                "\", " + String.valueOf(price) + ", " + String.valueOf(in_stock) +
                ", " + String.valueOf(price1) + ", true);";
        statement.execute(insert);
        int judge = -1;
        ResultSet temp = statement.executeQuery(sql);
        while (temp.next()) {
            judge = temp.getInt("count");
        }
        return judge == count + 1;
    }

    public boolean changeFoodOnSaleStatus(String sid) throws SQLException {
        String sql = "select on_sale from food where sid = " + sid + ";";
        ResultSet statusRes = statement.executeQuery(sql);
        boolean status = false;
        while (statusRes.next()) {
            status = statusRes.getBoolean("on_sale");
        }
        String update = "update food set on_sale = " + String.valueOf(!status) + " where sid=" + sid + ";";
        statement.execute(update);
        ResultSet newStatusSet = statement.executeQuery(sql);
        boolean judged = false;
        while (newStatusSet.next()) {
            judged = newStatusSet.getBoolean("on_sale");
        }
        return judged == status;
    }
}
