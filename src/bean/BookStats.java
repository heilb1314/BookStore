package bean;

public class BookStats {
    private String email;
    private double total;

    public BookStats(String email, double total) {
        super();
        this.email = email;
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}