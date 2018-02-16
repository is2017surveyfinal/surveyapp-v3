package it.survey.survey_is;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import it.survey.survey_is.model.Domanda;
import it.survey.survey_is.model.Risposta;
import it.survey.survey_is.model.Sondaggio;

public class SurveysUtils {

    public static final String SONDAGGI = "sondaggi";
    public static final String SONDAGGIO = "sondaggio";
    public static final String ID_SONDAGGIO = "ids";
    public static final String TITOLO = "titolo";
    public static final String DOMANDA = "domanda";
    public static final String ID_DOMANDA = "idq";
    public static final String FRUITORE = "fruitore";
    public static final String ARGOMENTO = "argomento";
    public static final String TESTO = "testo";
    public static final String IMMAGINE = "immagine";
    public static final String RISPOSTA = "risposta";
    public static final String ID_RISPOSTA = "id";
    public static final String CHECKED = "checked";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static List<Sondaggio> parseXml(String xmlTxt) {
        List<Sondaggio> lista = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringBufferInputStream(xmlTxt));
            is.setEncoding("ISO-8859-1");
            Document xml = builder.parse(is);
            NodeList sondaggi = xml.getElementsByTagName(SONDAGGIO);

            if (sondaggi.getLength() > 0) {
                for (int i=0; i<sondaggi.getLength(); i++) {
                    Sondaggio s = new Sondaggio();
                    lista.add(s);
                    Element sondaggio = (Element)sondaggi.item(i);
                    s.setId(sondaggio.getAttribute(ID_SONDAGGIO));

                    NodeList figli = sondaggio.getChildNodes();
                    for (int j=0; j<figli.getLength(); j++) {
                        Node node = figli.item(j);
                        if (node instanceof Element) {
                            Element figlio = (Element) node;
                            if (TITOLO.equals(figlio.getNodeName())) {
                                s.setTitle(figlio.getTextContent());
                            }
                            if (DOMANDA.equals(figlio.getNodeName())) {
                                Domanda d = new Domanda();
                                s.getDomande().add(d);
                                d.setId(figlio.getAttribute(ID_DOMANDA));
                                NodeList figli2 = figlio.getChildNodes();
                                for (int k = 0; k < figli2.getLength(); k++) {
                                    node = figli2.item(k);
                                    if (node instanceof Element) {
                                        Element figlio2 = (Element) node;
                                        if (ARGOMENTO.equals(figlio2.getNodeName())) {
                                            d.setArgomento(figlio2.getTextContent());
                                        }
                                        if (TESTO.equals(figlio2.getNodeName())) {
                                            d.setTesto(figlio2.getTextContent());
                                        }
                                        if (IMMAGINE.equals(figlio2.getNodeName())) {
                                            d.setImmagine(figlio2.getTextContent());
                                        }
                                        if (RISPOSTA.equals(figlio2.getNodeName())) {
                                            Risposta r = new Risposta();
                                            d.getRisposte().add(r);
                                            r.setId(figlio2.getAttribute(ID_RISPOSTA));
                                            r.setRisposta(figlio2.getTextContent());
                                            if (figlio2.getAttribute(CHECKED) != null) {
                                                r.setSelezionata(TRUE.equals(figlio2.getAttribute(CHECKED)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static String createXml(Sondaggio sondaggio, String fruitore) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        try {
            docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(SONDAGGI);
            doc.appendChild(rootElement);

            Element s = doc.createElement(SONDAGGIO);
            rootElement.appendChild(s);
            s.setAttribute(ID_SONDAGGIO, sondaggio.getId());

            Element t = doc.createElement(TITOLO);
            s.appendChild(t);
            t.appendChild(doc.createTextNode(sondaggio.getTitle()));

            Element f = doc.createElement(FRUITORE);
            s.appendChild(f);
            f.appendChild(doc.createTextNode(fruitore));

            for (Domanda domanda : sondaggio.getDomande()) {
                Element d = doc.createElement(DOMANDA);
                s.appendChild(d);
                d.setAttribute(ID_DOMANDA, domanda.getId());

                Element a = doc.createElement(ARGOMENTO);
                d.appendChild(a);
                a.appendChild(doc.createTextNode(domanda.getArgomento()));

                Element t2 = doc.createElement(TESTO);
                d.appendChild(t2);
                t2.appendChild(doc.createTextNode(domanda.getTesto()));

                Element i = doc.createElement(IMMAGINE);
                d.appendChild(i);
                i.appendChild(doc.createTextNode(domanda.getImmagine()));

                for (Risposta risposta : domanda.getRisposte()) {
                    Element r = doc.createElement(RISPOSTA);
                    d.appendChild(r);
                    r.setAttribute(ID_RISPOSTA, risposta.getId());
                    r.appendChild(doc.createTextNode(risposta.getRisposta()));
                    if (risposta.getSelezionata() != null && risposta.getSelezionata()) {
                        r.setAttribute(CHECKED, TRUE);
                    } else {
                        r.setAttribute(CHECKED, FALSE);
                    }
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

            return writer.toString();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }

        return "";
    }

}


