package root;

public class User {
    private String nom;
    private String prenom;
    private Integer age;

    public String getNom() {
        return nom;
    }

    public synchronized void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public synchronized void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Integer getAge() {
        return age;
    }

    public synchronized void setAge(Integer age) {
        this.age = age;
    }

    public String Imprime(String message){
        if( message != null){
            System.out.println("j'imprime................. "+ message);
            return message;
        }
        return "aucun parametre trouvé";
    }
}
