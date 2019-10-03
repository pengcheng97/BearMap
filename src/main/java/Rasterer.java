import java.util.HashMap;
import java.util.Map;
/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private double lrlon, ullon, w, h, ullat, lrlat;
    public Rasterer() {
        lrlon = 0;
        ullon = 0;
        w = 0;
        h = 0;
        ullat = 0;
        lrlat = 0;
    }
    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image;
     *                    can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        lrlon = params.get("lrlon");
        ullon = params.get("ullon");
        w = params.get("w");
        h = params.get("h");
        ullat = params.get("ullat");
        lrlat = params.get("lrlat");
        double lonDPP0 = 0.00034332275390625;
        double ullon0 = -122.2998046875;
        double lrlon0 = -122.2119140625;
        double ullat0 = 37.892195547244356;
        double lrlat0 = 37.82280243352756;
        double lonDPP = (lrlon - ullon) / w;
        double d = Math.log(lonDPP0 / lonDPP) / Math.log(2);
        int depth;
        if (d > 7) {
            depth = 7;
        } else if (d < 0) {
            depth = 0;
        } else {
            depth = (int) Math.ceil(d);
        }
//        System.out.println(depth);
        double length = (lrlon0 - ullon0) / Math.pow(2, depth);
        double height = (ullat0 - lrlat0) / Math.pow(2, depth);
        int x1 = (int) Math.floor((ullon - ullon0) / length);
        int x2 = (int) Math.ceil((lrlon - ullon0) / length);
        int y1 = (int) Math.floor((ullat0 - ullat) / height);
        int y2 = (int) Math.ceil((ullat0 - lrlat) / height);
        String[][] render = new String[y2 - y1][x2 - x1];
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                render[y - y1][x - x1] = "d" + String.valueOf(depth) + "_x"
                        + String.valueOf(x) + "_y" + String.valueOf(y) + ".png";
            }
        }
//        for (String[] i : render) {
//            for (String j : i) {
//                System.out.print(j);
//            }
//            System.out.println();
//        }
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", render);
        results.put("raster_ul_lon", ullon0 + x1 * length);
        results.put("raster_ul_lat", ullat0 - y1 * height);
        results.put("raster_lr_lon", ullon0 + x2 * length);
        results.put("raster_lr_lat", ullat0 - y2 * height);
        results.put("depth", depth);
        results.put("query_success", true);
//        System.out.println(results);
        return results;
    }
}
