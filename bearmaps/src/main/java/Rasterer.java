/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /** The max image depth level. */
    public static final int MAX_DEPTH = 7;

    /**
     * Takes a user query and finds the grid of images that best matches the query. These images
     * will be combined into one big image (rastered) by the front end. The grid of images must obey
     * the following properties, where image in the grid is referred to as a "tile".
     * <ul>
     *     <li>The tiles collected must cover the most longitudinal distance per pixel (LonDPP)
     *     possible, while still covering less than or equal to the amount of longitudinal distance
     *     per pixel in the query box for the user viewport size.</li>
     *     <li>Contains all tiles that intersect the query bounding box that fulfill the above
     *     condition.</li>
     *     <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * @param params The RasterRequestParams containing coordinates of the query box and the browser
     *               viewport width and height.
     * @return A valid RasterResultParams containing the computed results.
     */
    public RasterResultParams getMapRaster(RasterRequestParams params) {
        // build something
        // Set all the parameters into the built object, params.blahblah
        // Find depth using whatever, we used math
        // Find Boxes in which the query range lies in
        // Make corresponding String grid
        //
        if (params.ullat > MapServer.ROOT_ULLAT || params.ullon < MapServer.ROOT_ULLON
                || params.lrlat < MapServer.ROOT_LRLAT || params.lrlon > MapServer.ROOT_LRLON) {
            return RasterResultParams.queryFailed();
        }
        RasterResultParams.Builder result = new RasterResultParams.Builder();
        double lonDPP = ((params.lrlon - params.ullon) / params.w);

        int depth = (int) Math.ceil(Math.log(MapServer.ROOT_LONDPP / lonDPP) / Math.log(2));
        if (depth > 7) {
            depth = 7;
        }
        double sizexsquare = MapServer.ROOT_LON_DELTA / Math.pow(2, depth);
        double sizeysquare = MapServer.ROOT_LAT_DELTA / Math.pow(2, depth);
        int xstart = (int) Math.floor((params.ullon - MapServer.ROOT_ULLON) / sizexsquare);
        int ystart = (int) Math.floor((-params.ullat + MapServer.ROOT_ULLAT) / sizeysquare);
        int xend = (int) Math.floor((params.lrlon - MapServer.ROOT_ULLON) / sizexsquare);
        int yend = (int) Math.floor((MapServer.ROOT_ULLAT - params.lrlat) / sizeysquare);
        String[][] renderGrid = new String[yend - ystart + 1][xend - xstart + 1];
        for (int i = 0; i < renderGrid.length; i++) {
            for (int j = 0; j < renderGrid[0].length; j++) {
                renderGrid[i][j] = new String("d" + depth + "_x" + (xstart + j) + "_y"
                        + (ystart + i) + ".png");
            }
        }
        double ullon = (xstart * sizexsquare) + MapServer.ROOT_ULLON;
        double ullat = -(ystart * sizeysquare) + MapServer.ROOT_ULLAT;
        double lrlat = -(yend + 1) * sizeysquare + MapServer.ROOT_ULLAT;
        double lrlon = (xend + 1) * sizexsquare + MapServer.ROOT_ULLON;

        result.setDepth(depth);
        result.setRasterLrLat(lrlat);
        result.setRasterLrLon(lrlon);
        result.setRasterUlLat(ullat);
        result.setRasterUlLon(ullon);
        result.setRenderGrid(renderGrid);
        result.setQuerySuccess(true);
        /*
         * Hint: Define additional classes to make it easier to pass around multiple values, and
         * define additional methods to make it easier to test and reason about code. */
        RasterResultParams toReturn = result.create();
        return toReturn;
    }

    /**
     * Calculates the lonDPP of an image or query box
     * @param lrlon Lower right longitudinal value of the image or query box
     * @param ullon Upper left longitudinal value of the image or query box
     * @param width Width of the query box or image
     * @return lonDPP
     */
    private double lonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }
}
