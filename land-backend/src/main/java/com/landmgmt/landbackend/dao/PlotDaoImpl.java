package com.landmgmt.landbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.landmgmt.landbackend.model.Broker;
import com.landmgmt.landbackend.model.Plot;
import com.landmgmt.landbackend.model.Seller;

@Repository
public class PlotDaoImpl implements PlotDao {

    private final DataSource dataSource;

    public PlotDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Plot plot) {
        String sql = """
            INSERT INTO plots
              (gaata_no, land_id, plot_no, land_length, land_width,
               sell_rate, total_amount, status, seller_id, broker_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, plot.getGaataNo());
            ps.setLong(2, plot.getLandId());
            ps.setString(3, plot.getPlotNo());
            ps.setBigDecimal(4, plot.getLandLength());
            ps.setBigDecimal(5, plot.getLandWidth());
            ps.setBigDecimal(6, plot.getSellRate());
            ps.setBigDecimal(7, plot.getTotalAmount());
            ps.setString(8, plot.getStatus());
            setNullableLong(ps, 9,  plot.getSellerId());
            setNullableLong(ps, 10, plot.getBrokerId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save plot: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Plot plot) {
        String sql = """
            UPDATE plots SET
              gaata_no=?, plot_no=?, land_length=?, land_width=?,
              sell_rate=?, total_amount=?, status=?,
              seller_id=?, broker_id=?
            WHERE plot_id=?
            """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, plot.getGaataNo());
            ps.setString(2, plot.getPlotNo());
            ps.setBigDecimal(3, plot.getLandLength());
            ps.setBigDecimal(4, plot.getLandWidth());
            ps.setBigDecimal(5, plot.getSellRate());
            ps.setBigDecimal(6, plot.getTotalAmount());
            ps.setString(7, plot.getStatus());
            setNullableLong(ps, 8, plot.getSellerId());
            setNullableLong(ps, 9, plot.getBrokerId());
            ps.setLong(10, plot.getPlotId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update plot: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Plot> findByLandId(Long landId) {
        String sql = """
            SELECT p.*,
                   s.seller_id  AS s_id,   s.name    AS s_name,
                   s.contact_no AS s_phone, s.address AS s_addr, s.aadhar_no AS s_aadhar,
                   b.broker_id  AS b_id,   b.name    AS b_name,
                   b.contact_no AS b_phone, b.address AS b_addr, b.aadhar_no AS b_aadhar
            FROM plots p
            LEFT JOIN seller_details s ON p.seller_id = s.seller_id
            LEFT JOIN broker_details b ON p.broker_id = b.broker_id
            WHERE p.land_id = ?
            ORDER BY p.plot_id
            """;
        List<Plot> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, landId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPlot(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch plots: " + e.getMessage(), e);
        }
        return list;
    }

    // ── HELPERS ──────────────────────────────────────

    private void setNullableLong(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value != null && value > 0) ps.setLong(idx, value);
        else ps.setNull(idx, Types.BIGINT);
    }

    private Plot mapPlot(ResultSet rs) throws SQLException {
        Plot p = new Plot();
        p.setPlotId(rs.getLong("plot_id"));
        p.setGaataNo(rs.getString("gaata_no"));
        p.setLandId(rs.getLong("land_id"));
        p.setPlotNo(rs.getString("plot_no"));
        p.setLandLength(rs.getBigDecimal("land_length"));
        p.setLandWidth(rs.getBigDecimal("land_width"));
        p.setSellRate(rs.getBigDecimal("sell_rate"));
        p.setTotalAmount(rs.getBigDecimal("total_amount"));
        p.setStatus(rs.getString("status"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setBrokerId(rs.getLong("broker_id"));

        long sId = rs.getLong("s_id");
        if (!rs.wasNull()) {
            Seller seller = new Seller();
            seller.setSellerId(sId);
            seller.setName(rs.getString("s_name"));
            seller.setContactNo(rs.getString("s_phone"));
            seller.setAddress(rs.getString("s_addr"));
            seller.setAadharNo(rs.getString("s_aadhar"));
            p.setSeller(seller);
        }

        long bId = rs.getLong("b_id");
        if (!rs.wasNull()) {
            Broker broker = new Broker();
            broker.setBrokerId(bId);
            broker.setName(rs.getString("b_name"));
            broker.setContactNo(rs.getString("b_phone"));
            broker.setAddress(rs.getString("b_addr"));
            broker.setAadharNo(rs.getString("b_aadhar"));
            p.setBroker(broker);
        }

        return p;
    }
}