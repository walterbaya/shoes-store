package com.shoesstore.shoesstore.model;

import com.shoesstore.shoesstore.model.enums.PaymentMethod;
import com.shoesstore.shoesstore.model.enums.SaleChannel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private SaleChannel channel;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    private double shippingCost;
    private double discountPercentage;
    private double total;
    private double totalWithShippingCost;

    private LocalDateTime saleDate = LocalDateTime.now();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetails> details;

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Claim claim;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SaleChannel getChannel() {
		return channel;
	}

	public void setChannel(SaleChannel channel) {
		this.channel = channel;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public double getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

    public double getTotalWithShippingCost() {
        return totalWithShippingCost;
    }

    public void setTotalWithShippingCost(double totalWithShippingCost) {
        this.totalWithShippingCost = totalWithShippingCost;
    }

    public LocalDateTime getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(LocalDateTime saleDate) {
		this.saleDate = saleDate;
	}

	public List<SaleDetails> getDetails() {
		return details;
	}

	public void setDetails(List<SaleDetails> details) {
		this.details = details;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}
    
}
