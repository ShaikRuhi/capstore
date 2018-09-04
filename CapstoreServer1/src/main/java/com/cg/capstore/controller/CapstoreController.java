package com.cg.capstore.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cg.capstore.beans.AdminBean;
import com.cg.capstore.beans.CouponsBean;
import com.cg.capstore.beans.FeedbackProductBean;
import com.cg.capstore.beans.ImageBean;
import com.cg.capstore.beans.ProductBean;
import com.cg.capstore.exception.CapstoreException;
import com.cg.capstore.service.ICapstoreService;

@RestController
public class CapstoreController {

	@Autowired
	ICapstoreService service;

	
	  @RequestMapping(value="/generatingCoupons",method=RequestMethod.POST)
	  public String generateCoupon(String emailId,@RequestBody CouponsBean coupon) throws
	  CapstoreException {
	  
	  return service.createCoupon(emailId,coupon);
	 
	  }
	  
	  
	  
	  /*front end done by other group
	   * 
	   * @RequestMapping(value = "/applycoupons", method = RequestMethod.POST)
		public Double applyCoupon(String couponCode, Double price) throws CapstoreException {

			return service.applyCoupons(couponCode, price);

		}*/
	  
	  @RequestMapping(value = "/addingFeedback", method = RequestMethod.POST)
		public List<FeedbackProductBean> addingFeedback(String productId,@RequestBody FeedbackProductBean feedbackProductBean)
				throws CapstoreException {

			return service.addingfeedback(productId, feedbackProductBean);
		}

	  
	  @RequestMapping(value="/addFeedback",method=RequestMethod.POST)
		public FeedbackProductBean addAnFeedback(String productId,@RequestBody FeedbackProductBean feedbackProductBean) throws Exception {
			try {
				
				feedbackProductBean=service.addAnFeedback(productId, feedbackProductBean);
			} catch (CapstoreException e) {
				
				throw new Exception(e.getMessage());
			}
			return feedbackProductBean;
			
		}
	 

	
	

	

	
	@RequestMapping(value = "/addProductToCart", method = RequestMethod.POST)
	public List<ProductBean> addProductToCart(String emailId, String productId) throws CapstoreException {

		return service.addProductToCart(emailId, productId);
	}
	
	
	@RequestMapping(value = "/deleteProductFromCart", method = RequestMethod.POST)
	public List<ProductBean> removeProductFromCart(String emailId, String productId) throws CapstoreException {

	return	service.removeProductFromProduct(emailId, productId);
	}

	@RequestMapping(value = "/displayCart", method = RequestMethod.GET)
	public List<ProductBean> displayCart(String emailId) throws CapstoreException {

		return service.displayCart(emailId);
	}

	/*@RequestMapping(value = "/refreshCart", method = RequestMethod.GET)
	public List<ProductBean> refreshCart(String email) throws CapstoreException {

		return service.refreshCart(email);
	}
*/
	@RequestMapping(value = "/addImage", method = RequestMethod.POST)
	public String addImage(String productId,@RequestBody ImageBean image) throws FileNotFoundException, IOException {
 
		
		return service.addImage(productId,image);
	}

	/*correct one
	 * @RequestMapping(value = "/imageDisplay", method = RequestMethod.GET)
	public ImageBean showImage(String productId, HttpServletResponse response,
			HttpServletRequest request) throws ServletException, IOException {

		ImageBean image = service.get(productId);
		System.out.println(image);

		response.setContentType("image/jpeg; image/jpg; image/png; image/gif");
		// response.getOutputStream().write(image.getImg_data());
		response.getOutputStream().write(image.getImageData());

		response.getOutputStream().close();
 return image;
	}*/
	
	@RequestMapping(value = "/imageDisplay", method = RequestMethod.GET)
	public ResponseEntity<ImageBean> showImage(String productId, HttpServletResponse response,
			HttpServletRequest request) throws ServletException, IOException {

		ImageBean image = service.get(productId);
		//System.out.println(image);

		ResponseEntity<ImageBean> r=new ResponseEntity<ImageBean>(image, HttpStatus.OK);
		response.setContentType("image/jpeg; image/jpg; image/png; image/gif");
		response.getOutputStream().write(image.getImageData());
		response.getOutputStream().close();
		
        return r;
	}
	@RequestMapping(value="/getProduct",method=RequestMethod.GET)
	public ProductBean getProduct(String productId) {
		return service.getProduct(productId);
	}
	
	

}
