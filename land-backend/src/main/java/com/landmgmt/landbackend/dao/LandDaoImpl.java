package com.landmgmt.landbackend.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.landmgmt.landbackend.model.LandDetails;
import com.landmgmt.landbackend.model.Owner;

@Repository
public class LandDaoImpl implements LandDao {

    private final DataSource dataSource;

    public LandDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(LandDetails land) {
        String sql = """
            INSERT INTO land_details
              (gaata_number, location_address, length_in_sqft, width_in_sqft,
               purchase_rate_per_sqft, total_cost, paid_amount, balance_amount,
               contract_start_date, contract_end_date, owner_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setLandParams(ps, land);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save land: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(LandDetails land) {
        String sql = """
            UPDATE land_details SET
              gaata_number=?, location_address=?, length_in_sqft=?, width_in_sqft=?,
              purchase_rate_per_sqft=?, total_cost=?, paid_amount=?, balance_amount=?,
              contract_start_date=?, contract_end_date=?, owner_id=?
            WHERE land_id=?
            """;
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setLandParams(ps, land);
            ps.setLong(12, land.getLandId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update land: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LandDetails> findAll() {
        String sql = """
            SELECT l.*,
                   o.owner_id   AS o_id,
                   o.name       AS o_name,
                   o.contact_no AS o_phone,
                   o.address    AS o_addr,
                   o.aadhar_no  AS o_aadhar
            FROM land_details l
            LEFT JOIN owner_details o ON l.owner_id = o.owner_id
            ORDER BY l.land_id
            """;
        List<LandDetails> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapLand(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch lands: " + e.getMessage(), e);
        }
        return list;
    }

    // ── HELPERS ──────────────────────────────────────

    private void setLandParams(PreparedStatement ps, LandDetails land) throws SQLException {
        ps.setString(1, land.getGaataNumber());
        ps.setString(2, land.getLocationAddress());
        ps.setBigDecimal(3, land.getLengthInSqft());
        ps.setBigDecimal(4, land.getWidthInSqft());
        ps.setBigDecimal(5, land.getPurchaseRatePerSqft());
        ps.setBigDecimal(6, land.getTotalCost());
        ps.setBigDecimal(7, land.getPaidAmount());
        ps.setBigDecimal(8, land.getBalanceAmount());
        ps.setDate(9,  land.getContractStartDate() != null
                ? Date.valueOf(land.getContractStartDate()) : null);
        ps.setDate(10, land.getContractEndDate() != null
                ? Date.valueOf(land.getContractEndDate()) : null);
        if (land.getOwnerId() != null) ps.setLong(11, land.getOwnerId());
        else ps.setNull(11, Types.BIGINT);
    }

    private LandDetails mapLand(ResultSet rs) throws SQLException {
        LandDetails l = new LandDetails();
        l.setLandId(rs.getLong("land_id"));
        l.setGaataNumber(rs.getString("gaata_number"));
        l.setLocationAddress(rs.getString("location_address"));
        l.setLengthInSqft(rs.getBigDecimal("length_in_sqft"));
        l.setWidthInSqft(rs.getBigDecimal("width_in_sqft"));
        l.setPurchaseRatePerSqft(rs.getBigDecimal("purchase_rate_per_sqft"));
        l.setTotalCost(rs.getBigDecimal("total_cost"));
        l.setPaidAmount(rs.getBigDecimal("paid_amount"));
        l.setBalanceAmount(rs.getBigDecimal("balance_amount"));

        Date startDate = rs.getDate("contract_start_date");
        if (startDate != null) l.setContractStartDate(startDate.toLocalDate());

        Date endDate = rs.getDate("contract_end_date");
        if (endDate != null) l.setContractEndDate(endDate.toLocalDate());

        l.setOwnerId(rs.getLong("owner_id"));

        long oId = rs.getLong("o_id");
        if (!rs.wasNull()) {
            Owner owner = new Owner();
            owner.setOwnerId(oId);
            owner.setName(rs.getString("o_name"));
            owner.setContactNo(rs.getString("o_phone"));
            owner.setAddress(rs.getString("o_addr"));
            owner.setAadharNo(rs.getString("o_aadhar"));
            l.setOwner(owner);
        }
        return l;
    }
}