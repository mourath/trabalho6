package mongoProject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCursor;

public class MongoAppMain {

    public static MongoClient conectar() {
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
        return new MongoClient(uri);
    }

    public static void listaProdutos(MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("produtos");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    public static void insereProduto(MongoDatabase database, String nome, String descricao, double valor, String estado) {
        MongoCollection<Document> collection = database.getCollection("produtos");
        Document produto = new Document("nome", nome)
                .append("descricao", descricao)
                .append("valor", valor)
                .append("estado", estado);
        collection.insertOne(produto);
    }

    public static void alteraValorProduto(MongoDatabase database, String nome, double novoValor) {
        MongoCollection<Document> collection = database.getCollection("produtos");
        Document query = new Document("nome", nome);
        Document newValue = new Document("valor", novoValor);
        Document updateOperation = new Document("$set", newValue);
        collection.updateOne(query, updateOperation);
    }

    public static void apagaProduto(MongoDatabase database, String nome) {
        MongoCollection<Document> collection = database.getCollection("produtos");
        Document query = new Document("nome", nome);
        collection.deleteOne(query);
    }

    public static void fecharConexao(MongoClient mongoClient) {
        mongoClient.close();
    }

    public static void main(String[] args) {
        MongoClient mongoClient = conectar();
        MongoDatabase database = mongoClient.getDatabase("produtos");

        // (i) Imprime a lista original de produtos
        System.out.println("Lista Original de Produtos:");
        listaProdutos(database);

        // (ii) Insere um novo produto
        insereProduto(database, "Novo Produto", "Descrição do Novo Produto", 99.99, "novo");

        // (iii) Lista com o novo produto
        System.out.println("Lista com Novo Produto:");
        listaProdutos(database);

        // (iv) Altera o valor do produto inserido
        alteraValorProduto(database, "Novo Produto", 79.99);

        // (v) Lista com o valor do produto alterado
        System.out.println("Lista com Valor do Produto Alterado:");
        listaProdutos(database);

        // (vi) Apaga o produto que foi inserido
        apagaProduto(database, "Novo Produto");

        // (vii) Lista novamente de modo que seja igual à lista original
        System.out.println("Lista Final de Produtos:");
        listaProdutos(database);

        // (viii) Fecha a conexão
        fecharConexao(mongoClient);
    }
}
