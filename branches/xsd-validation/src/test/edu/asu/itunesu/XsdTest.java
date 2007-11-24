package test.edu.asu.itunesu;

import java.io.File;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

import edu.asu.itunesu.Course;
import edu.asu.itunesu.Division;
import edu.asu.itunesu.Group;
import edu.asu.itunesu.ITunesUDocument;
import edu.asu.itunesu.Permission;
import edu.asu.itunesu.Section;
import edu.asu.itunesu.Site;
import edu.asu.itunesu.Track;

public class XsdTest extends TestCase {
    public static String REQUEST_XSD_PATH = "iTunesURequest-1.0.2.xsd";
    public static String RESPONSE_XSD_PATH = "iTunesUResponse-1.0.2.xsd";

    private static Validator buildValidator(String path) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaLocation = new File(path);
        Schema schema = factory.newSchema(schemaLocation);
        return schema.newValidator();
    }

    private static Source buildSource(String xml) {
        return new StreamSource(new StringReader(xml));
    }

    private Validator requestValidator;
    // private Validator responseValidator;

    public void setUp() throws Exception {
        this.requestValidator = buildValidator(REQUEST_XSD_PATH);
        // this.responseValidator = buildValidator(RESPONSE_XSD_PATH);
    }

    public void testShowTree() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildShowTree("123456", "most");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeSite() throws Exception {
        Site site = new Site();
        ITunesUDocument doc = ITunesUDocument.buildMergeSite("123456", site, false, true);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeSiteNoHandle() throws Exception {
        Site site = new Site();
        ITunesUDocument doc = ITunesUDocument.buildMergeSite(null, site, false, true);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddDivision() throws Exception {
        Division division = new Division();
        ITunesUDocument doc = ITunesUDocument.buildAddDivision("123456", division);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteDivision() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteDivision("123456");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeDivision() throws Exception {
        Division division = new Division();
        ITunesUDocument doc = ITunesUDocument.buildMergeDivision("123456", division, true, false);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddSection() throws Exception {
        Section section = new Section();
        ITunesUDocument doc = ITunesUDocument.buildAddSection("123456", section);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteSection() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteSection("123456");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeSection() throws Exception {
        Section section = new Section();
        ITunesUDocument doc = ITunesUDocument.buildMergeSection("123456", section, false, false);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddCourse() throws Exception {
        Course course = new Course();
        ITunesUDocument doc = ITunesUDocument.buildAddCourse("123456", "234567", course);
        System.out.println(doc.toXml());
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddCourseNoTemplateHandle() throws Exception {
        Course course = new Course();
        ITunesUDocument doc = ITunesUDocument.buildAddCourse("123456", null, course);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteCourse() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteCourse("123456");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeCourse() throws Exception {
        Course course = new Course();
        ITunesUDocument doc = ITunesUDocument.buildMergeCourse("123456", course, true, true);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddGroup() throws Exception {
        Group group = new Group();
        ITunesUDocument doc = ITunesUDocument.buildAddGroup("123456", group);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteGroup() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteGroup("123456");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeGroup() throws Exception {
        Group group = new Group();
        ITunesUDocument doc = ITunesUDocument.buildMergeGroup("123456", group, true, false);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddTrack() throws Exception {
        Track track = new Track();
        ITunesUDocument doc = ITunesUDocument.buildAddTrack("123456", track);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteTrack() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteTrack("123456");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergeTrack() throws Exception {
        Track track = new Track();
        ITunesUDocument doc = ITunesUDocument.buildMergeTrack("123456", track);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddPermission() throws Exception {
        Permission permission = new Permission("credential", "access");
        ITunesUDocument doc = ITunesUDocument.buildAddPermission("123456", permission);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeletePermission() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeletePermission("123456", "credential");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testMergePermission() throws Exception {
        Permission permission = new Permission("credential", "access");
        ITunesUDocument doc = ITunesUDocument.buildMergePermission("123456", permission);
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testAddCredential() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildAddCredential("credential");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }

    public void testDeleteCredential() throws Exception {
        ITunesUDocument doc = ITunesUDocument.buildDeleteCredential("credential");
        this.requestValidator.validate(buildSource(doc.toXml()));
    }
}
