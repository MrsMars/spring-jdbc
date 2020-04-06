package com.aoher.repository.impl;

import com.aoher.model.Book;
import com.aoher.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcBookRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LobHandler lobHandler;

    @Override
    public int count() {
        return jdbcTemplate
                .queryForObject("select count(*) from books", Integer.class);
    }

    @Override
    public int save(Book book) {
        return jdbcTemplate.update(
                "insert into books (name, price) values(?,?)",
                book.getName(), book.getPrice()
        );
    }

    @Override
    public int update(Book book) {
        return jdbcTemplate.update(
                "update books set price = ? where id = ?",
                book.getPrice(), book.getId()
        );
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update(
                "delete books where id = ?", id
        );
    }

    @Override
    public List<Book> findAll() {
        return jdbcTemplate.query(
                "select * from books",
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                )
        );
    }

    @Override
    public List<Book> findByNameAndPrice(String name, BigDecimal price) {
        return jdbcTemplate.query(
                "select * from books where name like ? and price <= ?",
                new Object[] {"%" + name + "%", price },
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                )
        );
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "select * from books where id = ?",
                new Object[] {id},
                (rs, rowNum) -> Optional.of(new Book(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ))
        );
    }

    @Override
    public String findNameById(Long id) {
        return jdbcTemplate.queryForObject(
                "select name from books where id = ?",
                new Object[] {id},
                String.class
        );
    }

    @Override
    public int[] batchInsert(List<Book> books) {
        return jdbcTemplate.batchUpdate(
                "insert into books (name, price) values(?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, books.get(i).getName());
                        ps.setBigDecimal(2, books.get(i).getPrice());
                    }

                    @Override
                    public int getBatchSize() {
                        return books.size();
                    }
                }
        );
    }

    @Transactional
    @Override
    public int[][] batchInsert(List<Book> books, int batchSize) {
        return jdbcTemplate.batchUpdate(
                "insert into books (name, price) values(?,?)",
                books,
                batchSize,
                (ps, book) -> {
                    ps.setString(1, book.getName());
                    ps.setBigDecimal(2, book.getPrice());
                }
        );
    }

    @Override
    public int[] batchUpdate(List<Book> books) {
        return jdbcTemplate.batchUpdate(
                "update books set price = ? where id = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setBigDecimal(1, books.get(i).getPrice());
                        ps.setLong(2, books.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return books.size();
                    }
                }
        );
    }

    @Override
    public int[][] batchUpdate(List<Book> books, int batchSize) {
        return jdbcTemplate.batchUpdate(
                "update books set price = ? where id = ?",
                books,
                batchSize,
                (ps, book) -> {
                    ps.setBigDecimal(1, book.getPrice());
                    ps.setLong(2, book.getId());
                }
        );
    }

    @Override
    public void saveImage(Long bookId, File image) {
        try (InputStream imageStream = new FileInputStream(image)) {
            jdbcTemplate.execute(
                    "insert into book_image (book_id, filename, blob_image) values (?,?,?)",
                    new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                        @Override
                        protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                            ps.setLong(1, 1L);
                            ps.setString(2, image.getName());
                            lobCreator.setBlobAsBinaryStream(ps, 3, imageStream, (int) image.length());
                        }
                    }
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<Map<String, InputStream>> findImageByBookId(Long bookId) {
        return jdbcTemplate.query(
                "select id, book_id, filename, blob_image from book_image where book_id = ?",
                new Object[]{bookId},
                (rs, i) -> {
                    String fileName = rs.getString("filename");
                    InputStream blobImageStream = lobHandler.getBlobAsBinaryStream(rs, "blob_image");

                    Map<String, InputStream> result = new HashMap<>();
                    result.put(fileName, blobImageStream);
                    return result;
                }
        );
    }
}
