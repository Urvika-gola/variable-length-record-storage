/**
 * The Chips class represents information about a chip, such as its product name,
 * type, release date, and number of transistors.
 */

public class Chips {

    // Fields of the Chips class
    private String product; // Name of the product
    private String type; // Type of the chip
    private String releaseDate; // Release date of the product
    private String transistors; // Number of transistors in the chip

    // Getter and Setter for product field
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	
    // Getter and Setter for type field
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
    // Getter and Setter for releaseDate field
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
    // Getter and Setter for transistor field
	public String getTransistors() {
		return transistors;
	}
	public void setTransistors(String transistors) {
		this.transistors = transistors;
	}

    /**
     * Constructor to initialize the fields of the Chips class.
     * @param product The name of the product.
     * @param type The type of chip.
     * @param releaseDate The release date of the chip.
     * @param transistors The number of transistors on the chip.
     */
	public Chips(String product, String type, String releaseDate, String transistors) {
		super();
		this.product = product;
		this.type = type;
		this.releaseDate = releaseDate;
		this.transistors = transistors;
	}
	
    // Overrides the toString() method from the Object class
	@Override
	public String toString() {
		return "Chips [product=" + product + ", type=" + type + ", releaseDate=" + releaseDate + ", transistors="
				+ transistors + "]";
	}
	
    /**
     * Default constructor for the Chips class.
     * Initializes a new Chips object with no values assigned to its fields.
     */
    public Chips() {
        // No arguments passed to the constructor, fields will be initialized to default values
    }
}






