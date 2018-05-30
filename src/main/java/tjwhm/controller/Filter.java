package tjwhm.controller;

// Created by tjwhm at 2018/5/30 8:38 AM


import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import tjwhm.model.ClothesModel;
import tjwhm.model.FoodModel;
import tjwhm.model.bean.BaseBean;
import tjwhm.model.bean.ClothesBean;
import tjwhm.model.bean.FoodBean;

@Path("/filter")
@XmlRootElement
public class Filter {

    ClothesModel clothesModel = new ClothesModel();
    FoodModel foodModel = new FoodModel();

    @Path("/clothes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<List<ClothesBean>> getClothes(@QueryParam("name") String name,
                                                  @QueryParam("brand") String brand,
                                                  @QueryParam("color") String color,
                                                  @QueryParam("size") String size,
                                                  @QueryParam("gender") String gender,
                                                  @QueryParam("price_from") double price_from,
                                                  @QueryParam("price_to") double price_to,
                                                  @QueryParam("stock_from") int stock_from,
                                                  @QueryParam("stock_to") int stock_to,
                                                  @QueryParam("price1_from") double price1_from,
                                                  @QueryParam("price1_to") double price1_to,
                                                  @QueryParam("on_sale") String on_sale) throws SQLException {

        return new BaseBean<>(-1, "",
                clothesModel.getFitClothes(name, brand, color, size, gender, price_from, price_to, stock_from, stock_to, price1_from, price1_to, on_sale));

    }

    @Path("/food")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<List<FoodBean>> getFood(@QueryParam("name") String name,
                                            @QueryParam("brand") String brand,
                                            @QueryParam("shelf_life_from") String life_from,
                                            @QueryParam("shelf_life_to") String life_to,
                                            @QueryParam("origin") String origin,
                                            @QueryParam("price_from") double price_from,
                                            @QueryParam("price_to") double price_to,
                                            @QueryParam("stock_from") int stock_from,
                                            @QueryParam("stock_to") int stock_to,
                                            @QueryParam("price1_from") double price1_from,
                                            @QueryParam("price1_to") double price1_to,
                                            @QueryParam("on_sale") String on_sale) throws SQLException {
        return new BaseBean<>(-1, "", foodModel.getFitFood(name, brand, life_from, life_to, origin, price_from, price_to, stock_from, stock_to, price1_from, price1_to, on_sale));
    }

}
