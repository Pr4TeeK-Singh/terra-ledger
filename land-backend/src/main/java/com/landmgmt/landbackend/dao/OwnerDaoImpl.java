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

import com.landmgmt.landbackend.model.Owner;

@Repository
public class OwnerDaoImpl implements OwnerDao {

    private final DataSource dataSource;

    public OwnerDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(Owner owner) {
        String sql = "INSERT INTO owner_details (name, contact_no, address, aadhar_no) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, owner.getName());
            ps.setString(2, owner.getContactNo());
            ps.setString(3, owner.getAddress());
            ps.setString(4, owner.getAadharNo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save owner: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to retrieve generated key for owner");
    }

    @Override
    public void update(Owner owner) {
        String sql = "UPDATE owner_details SET name=?, contact_no=?, address=?, aadhar_no=? WHERE owner_id=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, owner.getName());
            ps.setString(2, owner.getContactNo());
            ps.setString(3, owner.getAddress());
            ps.setString(4, owner.getAadharNo());
            ps.setLong(5, owner.getOwnerId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update owner: " + e.getMessage(), e);
        }
    }

    @Override
    public Owner findById(Long ownerId) {
        String sql = "SELECT * FROM owner_details WHERE owner_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOwner(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find owner: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Owner> findAll() {
        String sql = "SELECT * FROM owner_details ORDER BY owner_id";
        List<Owner> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapOwner(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch owners: " + e.getMessage(), e);
        }
        return list;
    }

    private Owner mapOwner(ResultSet rs) throws SQLException {
        Owner o = new Owner();
        o.setOwnerId(rs.getLong("owner_id"));
        o.setName(rs.getString("name"));
        o.setContactNo(rs.getString("contact_no"));
        o.setAddress(rs.getString("address"));
        o.setAadharNo(rs.getString("aadhar_no"));
        return o;
    }
}