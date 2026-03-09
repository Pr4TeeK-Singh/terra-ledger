package com.landmgmt.landbackend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.landmgmt.landbackend.model.Seller;

@Repository
public class SellerDaoImpl implements SellerDao {

    private final DataSource dataSource;

    public SellerDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(Seller seller) {
        String sql = "INSERT INTO seller_details (name, contact_no, address, aadhar_no) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, seller.getName());
            ps.setString(2, seller.getContactNo());
            ps.setString(3, seller.getAddress());
            ps.setString(4, seller.getAadharNo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save seller: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to retrieve generated key for seller");
    }

    @Override
    public List<Seller> findAll() {
        String sql = "SELECT * FROM seller_details ORDER BY seller_id";
        List<Seller> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapSeller(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch sellers: " + e.getMessage(), e);
        }
        return list;
    }

    private Seller mapSeller(ResultSet rs) throws SQLException {
        Seller s = new Seller();
        s.setSellerId(rs.getLong("seller_id"));
        s.setName(rs.getString("name"));
        s.setContactNo(rs.getString("contact_no"));
        s.setAddress(rs.getString("address"));
        s.setAadharNo(rs.getString("aadhar_no"));
        return s;
    }
}