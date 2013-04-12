package com.glasscbe.sprighelloworld;

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.Utils;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	/**
	 * Atomic counter that we use to obtain a unique ID for each handler
	 * instance.
	 */
	private static AtomicInteger nextId = new AtomicInteger(1);

	/**
	 * The ID of this handler instance.
	 */
	private int handlerId;

	/**
	 * 
	 * 
	 * The application that will be run.
	 */
	@Autowired
	private CorpusController application;

	/**
	 * A corpus that will be used to hold the document being processed.
	 */
	private Corpus corpus;

	/**
	 * Set the application that will be run over the documents.
	 */
	public void setApplication(CorpusController application) {
		this.application = application;
	}

	/**
	 * Create the corpus. The PostConstruct annotation means that this method
	 * will be called by spring once the handler object has been constructed and
	 * its properties (i.e. the application) have been set.
	 */
	@PostConstruct
	public void init() throws Exception {
		handlerId = nextId.getAndIncrement();
		// log.info("init() for GateHandler " + handlerId);
		// create a corpus and give it to the controller
		corpus = Factory.newCorpus("webapp corpus");
		application.setCorpus(corpus);
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		return "home";
	}

	@RequestMapping(value = "/processtext", method = RequestMethod.POST)
	@ResponseBody
	public String analyzeQuery(HttpServletRequest req, HttpServletResponse res,
			Model model) {
		String question = (String) req.getParameter("text");

		Document doc = null;
		try {
			// log.debug("Creating document");
			doc = (Document) Factory
					.createResource("gate.corpora.DocumentImpl", Utils
							.featureMap("stringContent", question, "mimeType",
									""));
		} catch (ResourceInstantiationException e) {
			return "Could not create GATE document for input text";
		}

		try {
			corpus.add(doc);
			// log.info("Executing application");
			application.execute();
			return new Gson().toJson(doc);
		} catch (ExecutionException e) {
			return "Error occurred which executing GATE application";
		} finally {
			// clean-up tasks in a finally
			corpus.clear();
			// log.info("Deleting document");
			Factory.deleteResource(doc);
		}
	}
}
