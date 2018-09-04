package com.cg.capstore.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import com.cg.capstore.beans.AdminBean;
import com.cg.capstore.beans.CouponsBean;
import com.cg.capstore.beans.CustomerBean;
import com.cg.capstore.beans.FeedbackProductBean;
import com.cg.capstore.beans.ImageBean;
import com.cg.capstore.beans.ProductBean;
import com.cg.capstore.exception.CapstoreException;
import com.cg.capstore.repo.ICapstoreRepo;
import com.cg.capstore.repo.ICouponsRepo;
import com.cg.capstore.repo.ICustomerRepo;
import com.cg.capstore.repo.IFeedbackProductRepo;
import com.cg.capstore.repo.IImageRepo;
import com.cg.capstore.repo.IProductRepo;

@Service
public class CapstoreServiceImpl implements ICapstoreService {

	
	@Autowired
	ICapstoreRepo capstoreRepo;
	@Autowired
	IProductRepo productRepo;
	@Autowired
	IFeedbackProductRepo feedbackRepo;
	@Autowired
	ICouponsRepo couponsRepo;
	@Autowired
	ICustomerRepo customerRepo;
	@Autowired
	IImageRepo imageRepo;
	

	@Autowired
	private JavaMailSender javaMailSender;
	
	public CapstoreServiceImpl(JavaMailSender javaMailSender)
	{
		this.javaMailSender=javaMailSender;
		
	}
	@Transactional
	@Override
	public String createCoupon(String emailId, CouponsBean coupon) throws CapstoreException {
		CouponsBean coupon1=new CouponsBean();
		AdminBean admin1=capstoreRepo.getAdminDetails(emailId);
		if(admin1.getEmailId().equals(emailId)) {
		coupon1.setCouponCode(coupon.getCouponCode());
		coupon1.setCouponAmount(coupon.getCouponAmount());
		coupon1.setStartDate(coupon.getStartDate());
		coupon1.setEndDate(coupon.getEndDate());
		capstoreRepo.save(coupon1);
		//String output=sendEmail(emailId, coupon1);
		return (coupon1.getCouponCode()+" is generated and sent to all customers successfully");
		} 
			else {
				throw new CapstoreException("coupon cannot be generated");
			}
		
	}
	private String sendEmail( String emailId,CouponsBean coupon1)
	{
		List<String> customers=customerRepo.getCustomerEmailDetails();
		SimpleMailMessage simpleMailMessage=new SimpleMailMessage(); 
		for(int i=0; i<=customers.size();i++)
		{
		simpleMailMessage.setTo(customers.get(i));
		simpleMailMessage.setFrom(emailId);
		simpleMailMessage.setSubject("New Coupon");
		simpleMailMessage.setText(coupon1.getCouponCode()+" "+coupon1.getCouponAmount()+"% off");
		javaMailSender.send(simpleMailMessage);
		}
		return coupon1.getCouponCode();
		
	}
	

	@Override
	public Double applyCoupons(String couponCode,Double price) throws CapstoreException
	{
		CouponsBean coupon1=couponsRepo.getCouponDetails(couponCode);
		LocalDate startDate=coupon1.getStartDate().toLocalDate();
		LocalDate endDate=coupon1.getEndDate().toLocalDate();
		//Double totalPrice=0.0;
		if(coupon1.getCouponCode().equals(couponCode)&&startDate.isBefore(LocalDate.now())&&endDate.isAfter(LocalDate.now()))
		{
			Double totalPrice=price-((price*coupon1.getCouponAmount())/100);
			return totalPrice;
		}else {
			throw new CapstoreException("coupon cannot be applied");
		}
			
	}

	@Override
	public List<FeedbackProductBean> addingfeedback(String productId, FeedbackProductBean feedbackProductBean) throws CapstoreException {
		
		
		
		ProductBean product=productRepo.getOne(productId);
		if(product==null) {
			throw new CapstoreException("product not found");
		}else {
			
		
		FeedbackProductBean feedback=feedbackRepo.save(feedbackProductBean);
		product.getFeedbackProduct().add(feedback);
		
		productRepo.save(product);
		return product.getFeedbackProduct();
	}
	}


	
	
	@Transactional
	@Override
	public List<ProductBean> addProductToCart(String emailId, String productId) throws CapstoreException {

		CustomerBean customer = customerRepo.getCustomer(emailId);
		ProductBean product = productRepo.getProduct(productId);

		
		System.out.println("cus"+customer);
		System.out.println("pro"+product);
		if(customer==null) {
			throw new CapstoreException("customer doesnt exists");
		}else if(product==null){
			throw new CapstoreException("product doesnt exists");
		}else {
			
		
			customer.getCart().add(product);
			customerRepo.save(customer);
		

		return customer.getCart();
		}
	}
@Transactional
	@Override
	public List<ProductBean> removeProductFromProduct(String email, String productId) throws CapstoreException {

		CustomerBean customer = customerRepo.getCustomer(email);
		ProductBean product = productRepo.getProduct(productId);

		if(customer==null) {
			throw new CapstoreException("customer doesnt exists");
		}else if(product==null){
			throw new CapstoreException("product doesnt exists");
		}else
		{
			if (customer.getCart().contains(product)) 
			{
			customer.getCart().remove(product);
			}
			else {
				throw new CapstoreException("product is not in cart");
			}
			customerRepo.save(customer);
			return customer.getCart();
		}
	}

	@Override
	public List<ProductBean> displayCart(String emailId) throws CapstoreException {

		CustomerBean customer = customerRepo.getCustomer(emailId);
		if(customer==null) {
			throw new CapstoreException("customer doesnt exists");
		}
		else {
			return customer.getCart();
		}
		
	}

	@Transactional
	@Override
	public List<ProductBean> refreshCart(String email) throws CapstoreException {
		CustomerBean customer = customerRepo.getCustomer(email);
		if(customer==null) {
			throw new CapstoreException("customer doesnt exists");
		}
		else {
		if(customer.getCart().isEmpty()){
			System.out.println("cart is empty");
		}else {
			customer.getCart().clear();
		}
		customerRepo.save(customer);
		return customer.getCart();
	}
	}
	
	@Transactional
	@Override
	public String addImage(String productId,ImageBean image) throws IOException {
		ProductBean product=productRepo.getOne(productId);
		File file=new File(image.getImagePath());
		
		byte[] bfile=new byte[(int) file.length()];
		
		FileInputStream fileInputStream =new FileInputStream(file);

	     fileInputStream.read(bfile);
	     fileInputStream.close();
	     for(byte b : bfile) {     //Just to check whether bfile has any content
	         System.out.println(b +" ");
	     }
		
		image.setImageData(bfile);
		ImageBean image1=imageRepo.save(image);
		//imageRepo.save(image);
		
		product.getImage().add(image1);
	productRepo.save(product);
	    return "One image uploaded into database";
		
		
	}

	@Override
	public ImageBean get(String productId) {
		
		ProductBean product=productRepo.getProduct(productId);
		ImageBean image=	product.getImage().get(0);
		/*List<ImageBean> image = new ArrayList<ImageBean>();
		for(int i=0;i<product.getImage().size();i++) {
			image.add(product.getImage().get(i));
		}*/
		//ImageBean image=product.getImage(imageId);
		if(image==null) {
			System.out.println("not there");
		}
		else {
			System.out.println("there");
		}
		return image;
	}

	@Override
	public ProductBean getProduct(String productId) {
		return productRepo.getProduct(productId);
		
	}
	
	@Override
	public FeedbackProductBean addAnFeedback(String productId, FeedbackProductBean feedbackProductBean)
			throws CapstoreException {
		ProductBean productBean=productRepo.getOne(productId);
		if(productBean==null) {
			throw new CapstoreException("product not found");
		}else {
			
		
		FeedbackProductBean feedback=feedbackRepo.save(feedbackProductBean);
		productBean.getFeedbackProduct().add(feedback);
		
		productRepo.save(productBean);
		return feedback;
	}
		
	}
	
}
