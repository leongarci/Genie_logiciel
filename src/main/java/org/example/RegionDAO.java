package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegionDAO {

    public Region getRegionByName(EnumRegion region) {
        String sql = "SELECT * FROM public.region WHERE \"newreg\" = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, region.numeroReg());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur dans RegionDAO (getRegionByName) : " + e.getMessage());
        }
        return null;
    }

    public List<Region> getAllRegions() {
        List<Region> regions = new ArrayList<>();
        String sql = "SELECT * FROM public.region ORDER BY \"newreg\" ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                regions.add(mapResultSetToRegion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur dans RegionDAO (getAllRegions) : " + e.getMessage());
        }
        return regions;
    }

    private Region mapResultSetToRegion(ResultSet rs) throws SQLException {
        Region region = new Region();
        region.setNewreg(rs.getInt("newreg"));
        region.setNewregL(rs.getString("newreg_L"));
        region.setAnnee(rs.getInt("annee"));
        region.setAge0_4(rs.getInt("age_0_4"));
        region.setAge5_9(rs.getInt("age_5_9"));
        region.setAge10_14(rs.getInt("age_10_14"));
        region.setAge15_19(rs.getInt("age_15_19"));
        region.setAge20_24(rs.getInt("age_20_24"));
        region.setAge25_29(rs.getInt("age_25_29"));
        region.setAge30_34(rs.getInt("age_30_34"));
        region.setAge35_39(rs.getInt("age_35_39"));
        region.setAge40_44(rs.getInt("age_40_44"));
        region.setAge45_49(rs.getInt("age_45_49"));
        region.setAge50_54(rs.getInt("age_50_54"));
        region.setAge55_59(rs.getInt("age_55_59"));
        region.setAge60_64(rs.getInt("age_60_64"));
        region.setAge65_69(rs.getInt("age_65_69"));
        region.setAge70_74(rs.getInt("age_70_74"));
        region.setAge75_79(rs.getInt("age_75_79"));
        region.setAge80Plus(rs.getInt("age_80_plus"));
        region.setTotalGeneral(rs.getInt("total_general"));
        region.setRichesse(rs.getInt("richesse"));
        return region;
    }
}