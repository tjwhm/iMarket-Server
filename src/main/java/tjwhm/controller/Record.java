package tjwhm.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import tjwhm.model.bean.BaseBean;
import tjwhm.model.bean.RecordBean;
import tjwhm.model.RecordModel;

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
                                                 @QueryParam("shelf_life") String shelf_life,
                                                 @QueryParam("origin") String origin) throws SQLException {
        List<RecordBean> list = new ArrayList<>();
        if (type == TYPE_ALL) {
            list = recordModel.getRecord(after, before, name, brand, low, high);
        } else if (type == TYPE_CLOTHES) {
            list = recordModel.getRecord(after, before, name, brand, low, high, color, size, suitable_crowd);
        } else if (type == TYPE_FOOD) {
            list = recordModel.getRecord(after, before, name, brand, low, high, shelf_life, origin);
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
