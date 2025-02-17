package dao;

import domainModel.Document;

import java.util.List;

public interface DocumentDao extends DAO<Document, Integer>{
    public List<Document> getByOwner(int id) throws Exception;
    public List<Document> getByReceiver(int id) throws Exception;
}
