package com.shoesstore.shoesstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "proveedores")
@ToString(exclude = {"products", "supplierProducts"})
public class Supplier {
    @Id @GeneratedValue(strategy = IDENTITY) 
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Email @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String contactName;

    @Column(name = "cuit_cuil", nullable = false, unique = true, length = 13)
    private String cuit;  // Formato: XX-XXXXXXXX-X

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "supplier_payment_conditions",
            joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "payment_condition")
    @Enumerated(EnumType.STRING)
    private Set<PaymentCondition> paymentConditions = new HashSet<>();

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "supplier_product",
            joinColumns = @JoinColumn(name = "supplier_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "supplier_product_price", joinColumns = @JoinColumn(name = "supplier_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "price", nullable = false)
    private Map<Long, BigDecimal> productPrices = new HashMap<>();


    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierProduct> supplierProducts = new HashSet<>();

    @OneToMany(
            mappedBy = "supplier",
            cascade = CascadeType.ALL,       // Propaga persist, merge y remove
            orphanRemoval = true             // Elimina huérfanos al quitar de la colección
    )
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    public Supplier() {
    	
    }
    
	public Supplier(Long id, String name, @Email String email, String contactName, String cuit, String address,
			String phone, Set<PaymentCondition> paymentConditions, boolean active, Set<Product> products,
			Map<Long, BigDecimal> productPrices, Set<SupplierProduct> supplierProducts,
			List<PurchaseOrder> purchaseOrders) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.contactName = contactName;
		this.cuit = cuit;
		this.address = address;
		this.phone = phone;
		this.paymentConditions = paymentConditions;
		this.active = active;
		this.products = products;
		this.productPrices = productPrices;
		this.supplierProducts = supplierProducts;
		this.purchaseOrders = purchaseOrders;
	}





	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Set<PaymentCondition> getPaymentConditions() {
		return paymentConditions;
	}

	public void setPaymentConditions(Set<PaymentCondition> paymentConditions) {
		this.paymentConditions = paymentConditions;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Map<Long, BigDecimal> getProductPrices() {
		return productPrices;
	}

	public void setProductPrices(Map<Long, BigDecimal> productPrices) {
		this.productPrices = productPrices;
	}

	public Set<SupplierProduct> getSupplierProducts() {
		return supplierProducts;
	}

	public void setSupplierProducts(Set<SupplierProduct> supplierProducts) {
		this.supplierProducts = supplierProducts;
	}

	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supplier other = (Supplier) obj;
		return Objects.equals(id, other.id);
	}
}
