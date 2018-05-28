package tjwhm.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import tjwhm.model.RecordModel;
import tjwhm.model.bean.BaseBean;
import tjwhm.model.bean.RecordBean;

@Path("/record")
public class Record {
    private static final int TYPE_ALL = 0;
    private static final int TYPE_CLOTHES = 1;
    private static final int TYPE_FOOD = 2;

    private RecordModel recordModel = new RecordModel();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<List<RecordBean>> getRecords(@QueryParam("after") String after,
                                                 @QueryParam("before") String before,
                                                 @QueryParam("type") int type,
                                                 @QueryParam("name") String name,
                                                 @QueryParam("brand") String brand,
                                                 @QueryParam("color") String color,
                                                 @QueryParam("size") String size,
                                                 @QueryParam("suitable_crowd") String suitable_crowd,
                                                 @QueryParam("low") double low,
                                                 @QueryParam("high") double high,
                                                 @QueryParam("shelf_life_from") String shelf_life_from,
                                                 @QueryParam("shelf_life_to") String shelf_life_to,
                                                 @QueryParam("origin") String origin) throws SQLException {
        List<RecordBean> list = new ArrayList<>();
        System.out.println("after:" + after);
        System.out.println("before:" + before);
        System.out.println("type:" + String.valueOf(type));
        System.out.println("name:" + name);
        System.out.println("brand:" + brand);
        System.out.println("color:" + color);
        System.out.println("size:" + size);
        System.out.println("suitable_crowd:" + suitable_crowd);
        System.out.println("low:" + String.valueOf(low));
        System.out.println("high" + String.valueOf(high));
        System.out.println("shelf_life_from:" + shelf_life_from);
        System.out.println("shelf_life_from:" + shelf_life_to);
        System.out.println("origin:" + origin);
        if (type == TYPE_ALL) {
            list = recordModel.getRecord(after, before, name, brand, low, high);
        } else if (type == TYPE_CLOTHES) {
            list = recordModel.getRecordForClothes(after, before, name, brand, low, high, color, size, suitable_crowd);
        } else if (type == TYPE_FOOD) {
            list = recordModel.getRecordForFood(after, before, name, brand, low, high, shelf_life_from, shelf_life_to, origin);
        }

        return new BaseBean<>(-1, "", list);
    }

    @Path("/list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public BaseBean<List<RecordBean>> getAllRecords() throws SQLException {
        return new BaseBean<>(-1, "", recordModel.getAllRecords());
    }
}
