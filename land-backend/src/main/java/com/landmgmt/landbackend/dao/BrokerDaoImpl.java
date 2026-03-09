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

import com.landmgmt.landbackend.model.Broker;

@Repository
public class BrokerDaoImpl implements BrokerDao {

    private final DataSource dataSource;

    public BrokerDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(Broker broker) {
        String sql = "INSERT INTO broker_details (name, contact_no, address, aadhar_no) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, broker.getName());
            ps.setString(2, broker.getContactNo());
            ps.setString(3, broker.getAddress());
            ps.setString(4, broker.getAadharNo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save broker: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to retrieve generated key for broker");
    }

    @Override
    public List<Broker> findAll() {
        String sql = "SELECT * FROM broker_details ORDER BY broker_id";
        List<Broker> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapBroker(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch brokers: " + e.getMessage(), e);
        }
        return list;
    }

    private Broker mapBroker(ResultSet rs) throws SQLException {
        Broker b = new Broker();
        b.setBrokerId(rs.getLong("broker_id"));
        b.setName(rs.getString("name"));
        b.setContactNo(rs.getString("contact_no"));
        b.setAddress(rs.getString("address"));
        b.setAadharNo(rs.getString("aadhar_no"));
        return b;
    }
}