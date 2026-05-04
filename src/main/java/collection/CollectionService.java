package collection;

import java.util.List;

import carte.CartePossedee;

public class CollectionService {

    private CollectionDAO collectionDAO;

    public CollectionService() {
        this.collectionDAO = new CollectionDAO();
    }

    public List<CartePossedee> getCollectionUtilisateur(int userId) {
        return collectionDAO.getCollectionUtilisateur(userId);
    }
}
