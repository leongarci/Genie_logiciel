package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionService {
    private CollectionDAO collectionDAO;

    public CollectionService() {
        this.collectionDAO = new CollectionDAO();
    }

    public List<CartePossedee> getCollectionUtilisateur(int userId) {
        return collectionDAO.getCollectionUtilisateur(userId);
    }
}