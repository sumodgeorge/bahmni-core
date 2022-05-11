////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package org.openmrs.api;
//
//import java.lang.reflect.Field;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Set;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import org.apache.commons.lang3.time.DateUtils;
//import org.hamcrest.Matcher;
//import org.hamcrest.MatcherAssert;
//import org.hamcrest.Matchers;
//import org.hibernate.boot.Metadata;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.openmrs.Allergy;
//import org.openmrs.CareSetting;
//import org.openmrs.Concept;
//import org.openmrs.ConceptClass;
//import org.openmrs.ConceptDatatype;
//import org.openmrs.ConceptDescription;
//import org.openmrs.ConceptName;
//import org.openmrs.Condition;
//import org.openmrs.Diagnosis;
//import org.openmrs.DosingInstructions;
//import org.openmrs.Drug;
//import org.openmrs.DrugOrder;
//import org.openmrs.Encounter;
//import org.openmrs.FreeTextDosingInstructions;
//import org.openmrs.GlobalProperty;
//import org.openmrs.Obs;
//import org.openmrs.Order;
//import org.openmrs.OrderFrequency;
//import org.openmrs.OrderGroup;
//import org.openmrs.OrderGroupAttribute;
//import org.openmrs.OrderGroupAttributeType;
//import org.openmrs.OrderSet;
//import org.openmrs.OrderType;
//import org.openmrs.Patient;
//import org.openmrs.Provider;
//import org.openmrs.SimpleDosingInstructions;
//import org.openmrs.TestOrder;
//import org.openmrs.Visit;
//import org.openmrs.Order.Action;
//import org.openmrs.Order.FulfillerStatus;
//import org.openmrs.Order.Urgency;
//import org.openmrs.api.builder.DrugOrderBuilder;
//import org.openmrs.api.builder.OrderBuilder;
//import org.openmrs.api.context.Context;
//import org.openmrs.api.db.hibernate.HibernateAdministrationDAO;
//import org.openmrs.api.db.hibernate.HibernateSessionFactoryBean;
//import org.openmrs.customdatatype.datatype.FreeTextDatatype;
//import org.openmrs.messagesource.MessageSourceService;
//import org.openmrs.order.OrderUtil;
//import org.openmrs.order.OrderUtilTest;
//import org.openmrs.parameter.OrderSearchCriteria;
//import org.openmrs.parameter.OrderSearchCriteriaBuilder;
//import org.openmrs.test.OpenmrsMatchers;
//import org.openmrs.test.TestUtil;
//import org.openmrs.test.jupiter.BaseContextSensitiveTest;
//import org.openmrs.util.DateUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class OrderServiceTest extends BaseContextSensitiveTest {
//    private static final String OTHER_ORDER_FREQUENCIES_XML = "org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml";
//    protected static final String ORDER_SET = "org/openmrs/api/include/OrderSetServiceTest-general.xml";
//    private static final String ORDER_GROUP_ATTRIBUTES = "org/openmrs/api/include/OrderServiceTest-createOrderGroupAttributes.xml";
//    @Autowired
//    private ConceptService conceptService;
//    @Autowired
//    private OrderService orderService;
//    @Autowired
//    private PatientService patientService;
//    @Autowired
//    private EncounterService encounterService;
//    @Autowired
//    private ProviderService providerService;
//    @Autowired
//    private AdministrationService adminService;
//    @Autowired
//    private OrderSetService orderSetService;
//    @Autowired
//    private MessageSourceService messageSourceService;
//
//    public OrderServiceTest() {
//    }
//
//    @BeforeEach
//    public void setUp() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-createOrderGroupAttributes.xml");
//    }
//
//    @Test
//    public void saveOrder_shouldNotSaveOrderIfOrderDoesntValidate() {
//        Order order = new Order();
//        order.setPatient((Patient)null);
//        order.setOrderer((Provider)null);
//        APIException exception = (APIException)Assertions.assertThrows(APIException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.containsString("failed to validate with reason:"));
//    }
//
//    @Test
//    public void getOrderByUuid_shouldFindObjectGivenValidUuid() {
//        String uuid = "921de0a3-05c4-444a-be03-e01b4c4b9142";
//        Order order = this.orderService.getOrderByUuid(uuid);
//        Assertions.assertEquals(1, order.getOrderId());
//    }
//
//    @Test
//    public void getOrderByUuid_shouldReturnNullIfNoObjectFoundWithGivenUuid() {
//        Assertions.assertNull(this.orderService.getOrderByUuid("some invalid uuid"));
//    }
//
//    @Test
//    public void purgeOrder_shouldDeleteAnyObsAssociatedToTheOrderWhenCascadeIsTrue() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-deleteObsThatReference.xml");
//        String ordUuid = "0c96f25c-4949-4f72-9931-d808fbcdb612";
//        String obsUuid = "be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4";
//        ObsService os = Context.getObsService();
//        Obs obs = os.getObsByUuid("be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4");
//        Assertions.assertNotNull(obs);
//        Order order = this.orderService.getOrderByUuid("0c96f25c-4949-4f72-9931-d808fbcdb612");
//        Assertions.assertNotNull(order);
//        Assertions.assertEquals(order, obs.getOrder());
//        this.orderService.purgeOrder(order, false);
//        Assertions.assertNotNull(os.getObsByUuid("be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4"));
//        this.orderService.purgeOrder(order, true);
//        Assertions.assertNull(this.orderService.getOrderByUuid("0c96f25c-4949-4f72-9931-d808fbcdb612"));
//        Assertions.assertNull(os.getObsByUuid("be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4"));
//    }
//
//    @Test
//    public void purgeOrder_shouldDeleteOrderFromTheDatabase() {
//        String uuid = "9c21e407-697b-11e3-bd76-0800271c1b75";
//        Order order = this.orderService.getOrderByUuid("9c21e407-697b-11e3-bd76-0800271c1b75");
//        Assertions.assertNotNull(order);
//        this.orderService.purgeOrder(order);
//        Assertions.assertNull(this.orderService.getOrderByUuid("9c21e407-697b-11e3-bd76-0800271c1b75"));
//    }
//
//    @Test
//    public void getNewOrderNumber_shouldAlwaysReturnUniqueOrderNumbersWhenCalledMultipleTimesWithoutSavingOrders() throws InterruptedException {
//        int N = 50;
//        Set<String> uniqueOrderNumbers = new HashSet(50);
//        List<Thread> threads = new ArrayList();
//
//        int i;
//        for(i = 0; i < N; ++i) {
//            threads.add(new Thread(() -> {
//                try {
//                    Context.openSession();
//                    Context.addProxyPrivilege("Add Orders");
//                    uniqueOrderNumbers.add(((OrderNumberGenerator)this.orderService).getNewOrderNumber((OrderContext)null));
//                } finally {
//                    Context.removeProxyPrivilege("Add Orders");
//                    Context.closeSession();
//                }
//
//            }));
//        }
//
//        for(i = 0; i < N; ++i) {
//            ((Thread)threads.get(i)).start();
//        }
//
//        for(i = 0; i < N; ++i) {
//            ((Thread)threads.get(i)).join();
//        }
//
//        Assertions.assertEquals(N, uniqueOrderNumbers.size());
//    }
//
//    @Test
//    public void getOrderByOrderNumber_shouldFindObjectGivenValidOrderNumber() {
//        Order order = this.orderService.getOrderByOrderNumber("1");
//        Assertions.assertNotNull(order);
//        Assertions.assertEquals(1, order.getOrderId());
//    }
//
//    @Test
//    public void getOrderByOrderNumber_shouldReturnNullIfNoObjectFoundWithGivenOrderNumber() {
//        Assertions.assertNull(this.orderService.getOrderByOrderNumber("some invalid order number"));
//    }
//
//    @Test
//    public void getOrderHistoryByConcept_shouldReturnOrdersWithTheGivenConcept() {
//        Concept concept = Context.getConceptService().getConcept(88);
//        Patient patient = Context.getPatientService().getPatient(2);
//        List<Order> orders = this.orderService.getOrderHistoryByConcept(patient, concept);
//        Assertions.assertEquals(3, orders.size());
//        Assertions.assertEquals(444, ((Order)orders.get(0)).getOrderId());
//        Assertions.assertEquals(44, ((Order)orders.get(1)).getOrderId());
//        Assertions.assertEquals(4, ((Order)orders.get(2)).getOrderId());
//        concept = Context.getConceptService().getConcept(792);
//        orders = this.orderService.getOrderHistoryByConcept(patient, concept);
//        Assertions.assertEquals(4, orders.size());
//        Assertions.assertEquals(3, ((Order)orders.get(0)).getOrderId());
//        Assertions.assertEquals(222, ((Order)orders.get(1)).getOrderId());
//        Assertions.assertEquals(22, ((Order)orders.get(2)).getOrderId());
//        Assertions.assertEquals(2, ((Order)orders.get(3)).getOrderId());
//    }
//
//    @Test
//    public void getOrderHistoryByConcept_shouldReturnEmptyListForConceptWithoutOrders() {
//        Concept concept = Context.getConceptService().getConcept(21);
//        Patient patient = Context.getPatientService().getPatient(2);
//        List<Order> orders = this.orderService.getOrderHistoryByConcept(patient, concept);
//        Assertions.assertEquals(0, orders.size());
//    }
//
//    @Test
//    public void getOrderHistoryByConcept_shouldRejectANullConcept() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getOrderHistoryByConcept(new Patient(), (Concept)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("patient and concept are required"));
//    }
//
//    @Test
//    public void getOrderHistoryByConcept_shouldRejectANullPatient() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getOrderHistoryByConcept((Patient)null, new Concept());
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("patient and concept are required"));
//    }
//
//    @Test
//    public void getOrderHistoryByOrderNumber_shouldReturnAllOrderHistoryForGivenOrderNumber() {
//        List<Order> orders = this.orderService.getOrderHistoryByOrderNumber("111");
//        Assertions.assertEquals(2, orders.size());
//        Assertions.assertEquals(111, ((Order)orders.get(0)).getOrderId());
//        Assertions.assertEquals(1, ((Order)orders.get(1)).getOrderId());
//    }
//
//    @Test
//    public void getOrderFrequency_shouldReturnTheOrderFrequencyThatMatchesTheSpecifiedId() {
//        Assertions.assertEquals("28090760-7c38-11e3-baa7-0800200c9a66", this.orderService.getOrderFrequency(1).getUuid());
//    }
//
//    @Test
//    public void getOrderFrequencyByUuid_shouldReturnTheOrderFrequencyThatMatchesTheSpecifiedUuid() {
//        Assertions.assertEquals(1, this.orderService.getOrderFrequencyByUuid("28090760-7c38-11e3-baa7-0800200c9a66").getOrderFrequencyId());
//    }
//
//    @Test
//    public void getOrderFrequencyByConcept_shouldReturnTheOrderFrequencyThatMatchesTheSpecifiedConcept() {
//        Concept concept = this.conceptService.getConcept(4);
//        Assertions.assertEquals(3, this.orderService.getOrderFrequencyByConcept(concept).getOrderFrequencyId());
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldReturnOnlyNonRetiredOrderFrequenciesIfIncludeRetiredIsSetToFalse() {
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies(false);
//        Assertions.assertEquals(2, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 1));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 2));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldReturnAllTheOrderFrequenciesIfIncludeRetiredIsSetToTrue() {
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies(true);
//        Assertions.assertEquals(3, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 1));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 2));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 3));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnAllActiveOrdersForTheSpecifiedPatient() {
//        Patient patient = Context.getPatientService().getPatient(2);
//        List<Order> orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(5, orders.size());
//        Order[] expectedOrders = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5), this.orderService.getOrder(7)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders));
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder((Order)orders.get(0), (Date)null));
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder((Order)orders.get(1), (Date)null));
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder((Order)orders.get(2), (Date)null));
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder((Order)orders.get(3), (Date)null));
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder((Order)orders.get(4), (Date)null));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnAllActiveOrdersForTheSpecifiedPatientAndCareSetting() {
//        Patient patient = this.patientService.getPatient(2);
//        CareSetting careSetting = this.orderService.getCareSetting(1);
//        List<Order> orders = this.orderService.getActiveOrders(patient, (OrderType)null, careSetting, (Date)null);
//        Assertions.assertEquals(4, orders.size());
//        Order[] expectedOrders = new Order[]{this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5), this.orderService.getOrder(7)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnAllActiveDrugOrdersForTheSpecifiedPatient() {
//        Patient patient = this.patientService.getPatient(2);
//        List<Order> orders = this.orderService.getActiveOrders(patient, this.orderService.getOrderType(1), (CareSetting)null, (Date)null);
//        Assertions.assertEquals(4, orders.size());
//        Order[] expectedOrders = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnAllActiveTestOrdersForTheSpecifiedPatient() {
//        Patient patient = this.patientService.getPatient(2);
//        List<Order> orders = this.orderService.getActiveOrders(patient, this.orderService.getOrderTypeByName("Test order"), (CareSetting)null, (Date)null);
//        Assertions.assertEquals(1, orders.size());
//        Assertions.assertEquals(orders.get(0), this.orderService.getOrder(7));
//    }
//
//    @Test
//    public void getActiveOrders_shouldFailIfPatientIsNull() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getActiveOrders((Patient)null, (OrderType)null, this.orderService.getCareSetting(1), (Date)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Patient is required when fetching active orders"));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnActiveOrdersAsOfTheSpecifiedDate() throws ParseException {
//        Patient patient = Context.getPatientService().getPatient(2);
//        List<Order> orders = this.orderService.getAllOrdersByPatient(patient);
//        Assertions.assertEquals(12, orders.size());
//        Date asOfDate = Context.getDateFormat().parse("10/12/2007");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(2, orders.size());
//        Assertions.assertFalse(orders.contains(this.orderService.getOrder(22)));
//        Assertions.assertFalse(orders.contains(this.orderService.getOrder(44)));
//        Assertions.assertFalse(orders.contains(this.orderService.getOrder(8)));
//        Order[] expectedOrders = new Order[]{this.orderService.getOrder(9)};
//        asOfDate = Context.getDateTimeFormat().parse("10/12/2007 00:01:00");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(1, orders.size());
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders));
//        Order[] expectedOrders1 = new Order[]{this.orderService.getOrder(3), this.orderService.getOrder(4), this.orderService.getOrder(222)};
//        asOfDate = Context.getDateFormat().parse("10/04/2008");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(3, orders.size());
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders1));
//        asOfDate = Context.getDateTimeFormat().parse("10/04/2008 00:01:00");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(2, orders.size());
//        Order[] expectedOrders2 = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders2));
//        Order[] expectedOrders3 = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5), this.orderService.getOrder(6)};
//        asOfDate = Context.getDateTimeFormat().parse("26/09/2008 09:24:10");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(5, orders.size());
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders3));
//        asOfDate = Context.getDateTimeFormat().parse("26/09/2008 09:25:10");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(4, orders.size());
//        Order[] expectedOrders4 = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders4));
//        asOfDate = Context.getDateFormat().parse("04/12/2008");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(5, orders.size());
//        Order[] expectedOrders5 = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5), this.orderService.getOrder(7)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders5));
//        asOfDate = Context.getDateFormat().parse("06/12/2008");
//        orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, asOfDate);
//        Assertions.assertEquals(5, orders.size());
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders5));
//    }
//
//    @Test
//    public void getActiveOrders_shouldReturnAllOrdersIfNoOrderTypeIsSpecified() {
//        Patient patient = Context.getPatientService().getPatient(2);
//        List<Order> orders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(5, orders.size());
//        Order[] expectedOrders = new Order[]{this.orderService.getOrder(222), this.orderService.getOrder(3), this.orderService.getOrder(444), this.orderService.getOrder(5), this.orderService.getOrder(7)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrders));
//    }
//
//    @Test
//    public void getActiveOrders_shouldIncludeOrdersForSubTypesIfOrderTypeIsSpecified() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrders.xml");
//        Patient patient = Context.getPatientService().getPatient(2);
//        OrderType testOrderType = this.orderService.getOrderType(2);
//        List<Order> orders = this.orderService.getActiveOrders(patient, testOrderType, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(5, orders.size());
//        Order[] expectedOrder1 = new Order[]{this.orderService.getOrder(7), this.orderService.getOrder(101), this.orderService.getOrder(102), this.orderService.getOrder(103), this.orderService.getOrder(104)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrder1));
//        OrderType labTestOrderType = this.orderService.getOrderType(7);
//        orders = this.orderService.getActiveOrders(patient, labTestOrderType, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(3, orders.size());
//        Order[] expectedOrder2 = new Order[]{this.orderService.getOrder(101), this.orderService.getOrder(103), this.orderService.getOrder(104)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrder2));
//    }
//
//    @Test
//    public void discontinueOrder_shouldPopulateCorrectAttributesOnTheDiscontinueAndDiscontinuedOrders() {
//        Order order = this.orderService.getOrderByOrderNumber("111");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Provider orderer = this.providerService.getProvider(1);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Date discontinueDate = new Date();
//        String discontinueReasonNonCoded = "Test if I can discontinue this";
//        Order discontinueOrder = this.orderService.discontinueOrder(order, discontinueReasonNonCoded, discontinueDate, orderer, encounter);
//        Assertions.assertEquals(order.getDateStopped(), discontinueDate);
//        Assertions.assertNotNull(discontinueOrder);
//        Assertions.assertNotNull(discontinueOrder.getId());
//        Assertions.assertEquals(discontinueOrder.getDateActivated(), discontinueOrder.getAutoExpireDate());
//        Assertions.assertEquals(discontinueOrder.getAction(), Action.DISCONTINUE);
//        Assertions.assertEquals(discontinueOrder.getOrderReasonNonCoded(), discontinueReasonNonCoded);
//        Assertions.assertEquals(discontinueOrder.getPreviousOrder(), order);
//    }
//
//    @Test
//    public void discontinueOrder_shouldPassForAnActiveOrderWhichIsScheduledAndNotStartedAsOfDiscontinueDate() {
//        Order order = new Order();
//        order.setAction(Action.NEW);
//        order.setPatient(Context.getPatientService().getPatient(7));
//        order.setConcept(Context.getConceptService().getConcept(5497));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setOrderer(this.orderService.getOrder(1).getOrderer());
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderType(this.orderService.getOrderType(17));
//        order.setDateActivated(new Date());
//        order.setScheduledDate(DateUtils.addMonths(new Date(), 2));
//        order.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        order = this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Assertions.assertFalse(order.isStarted());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Provider orderer = this.providerService.getProvider(1);
//        Date discontinueDate = new Date();
//        String discontinueReasonNonCoded = "Test if I can discontinue this";
//        Order discontinueOrder = this.orderService.discontinueOrder(order, discontinueReasonNonCoded, discontinueDate, orderer, encounter);
//        Assertions.assertEquals(order.getDateStopped(), discontinueDate);
//        Assertions.assertNotNull(discontinueOrder);
//        Assertions.assertNotNull(discontinueOrder.getId());
//        Assertions.assertEquals(discontinueOrder.getDateActivated(), discontinueOrder.getAutoExpireDate());
//        Assertions.assertEquals(discontinueOrder.getAction(), Action.DISCONTINUE);
//        Assertions.assertEquals(discontinueOrder.getOrderReasonNonCoded(), discontinueReasonNonCoded);
//        Assertions.assertEquals(discontinueOrder.getPreviousOrder(), order);
//    }
//
//    @Test
//    public void discontinueOrder_shouldSetCorrectAttributesOnTheDiscontinueAndDiscontinuedOrders() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-discontinueReason.xml");
//        Order order = this.orderService.getOrderByOrderNumber("111");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Provider orderer = this.providerService.getProvider(1);
//        Date discontinueDate = new Date();
//        Concept concept = Context.getConceptService().getConcept(1);
//        Order discontinueOrder = this.orderService.discontinueOrder(order, concept, discontinueDate, orderer, encounter);
//        Assertions.assertEquals(order.getDateStopped(), discontinueDate);
//        Assertions.assertNotNull(discontinueOrder);
//        Assertions.assertNotNull(discontinueOrder.getId());
//        Assertions.assertEquals(discontinueOrder.getDateActivated(), discontinueOrder.getAutoExpireDate());
//        Assertions.assertEquals(discontinueOrder.getAction(), Action.DISCONTINUE);
//        Assertions.assertEquals(discontinueOrder.getOrderReason(), concept);
//        Assertions.assertEquals(discontinueOrder.getPreviousOrder(), order);
//    }
//
//    @Test
//    public void discontinueOrder_shouldPassForAnActiveOrderWhichIsScheduledAndNotStartedAsOfDiscontinueDateWithParamConcept() {
//        Order order = new Order();
//        order.setAction(Action.NEW);
//        order.setPatient(Context.getPatientService().getPatient(7));
//        order.setConcept(Context.getConceptService().getConcept(5497));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setOrderer(this.orderService.getOrder(1).getOrderer());
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderType(this.orderService.getOrderType(17));
//        order.setDateActivated(new Date());
//        order.setScheduledDate(DateUtils.addMonths(new Date(), 2));
//        order.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        order = this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Assertions.assertFalse(order.isStarted());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Provider orderer = this.providerService.getProvider(1);
//        Date discontinueDate = new Date();
//        Concept concept = Context.getConceptService().getConcept(1);
//        Order discontinueOrder = this.orderService.discontinueOrder(order, concept, discontinueDate, orderer, encounter);
//        Assertions.assertEquals(order.getDateStopped(), discontinueDate);
//        Assertions.assertNotNull(discontinueOrder);
//        Assertions.assertNotNull(discontinueOrder.getId());
//        Assertions.assertEquals(discontinueOrder.getDateActivated(), discontinueOrder.getAutoExpireDate());
//        Assertions.assertEquals(discontinueOrder.getAction(), Action.DISCONTINUE);
//        Assertions.assertEquals(discontinueOrder.getOrderReason(), concept);
//        Assertions.assertEquals(discontinueOrder.getPreviousOrder(), order);
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailForADiscontinuationOrder() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-discontinuedOrder.xml");
//        Order discontinuationOrder = this.orderService.getOrder(26);
//        Assertions.assertEquals(Action.DISCONTINUE, discontinuationOrder.getAction());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        CannotStopDiscontinuationOrderException exception = (CannotStopDiscontinuationOrderException)Assertions.assertThrows(CannotStopDiscontinuationOrderException.class, () -> {
//            this.orderService.discontinueOrder(discontinuationOrder, "Test if I can discontinue this", (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.action.cannot.discontinue")));
//    }
//
//    @Test
//    public void discontinueOrder_shouldNotPassForADiscontinuationOrder() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-discontinuedOrder.xml");
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-discontinueReason.xml");
//        Order discontinuationOrder = this.orderService.getOrder(26);
//        Assertions.assertEquals(Action.DISCONTINUE, discontinuationOrder.getAction());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        CannotStopDiscontinuationOrderException exception = (CannotStopDiscontinuationOrderException)Assertions.assertThrows(CannotStopDiscontinuationOrderException.class, () -> {
//            this.orderService.discontinueOrder(discontinuationOrder, (Concept)null, (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.action.cannot.discontinue")));
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailForADiscontinuedOrder() {
//        Order discontinuationOrder = this.orderService.getOrder(2);
//        Assertions.assertFalse(discontinuationOrder.isActive());
//        Assertions.assertNotNull(discontinuationOrder.getDateStopped());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.discontinueOrder(discontinuationOrder, "some reason", (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void discontinueOrder_shouldNotPassForADiscontinuedOrder() {
//        Order discontinuationOrder = this.orderService.getOrder(2);
//        Assertions.assertFalse(discontinuationOrder.isActive());
//        Assertions.assertNotNull(discontinuationOrder.getDateStopped());
//        Encounter encounter = this.encounterService.getEncounter(3);
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.discontinueOrder(discontinuationOrder, (Concept)null, (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveOrder_shouldDiscontinueExistingActiveOrderIfNewOrderBeingSavedWithActionToDiscontinue() {
//        DrugOrder order = new DrugOrder();
//        order.setAction(Action.DISCONTINUE);
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setDrug(this.conceptService.getDrug(3));
//        order.setEncounter(this.encounterService.getEncounter(5));
//        order.setPatient(this.patientService.getPatient(7));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderType(this.orderService.getOrderType(1));
//        order.setDateActivated(new Date());
//        order.setDosingType(SimpleDosingInstructions.class);
//        order.setDose(500.0D);
//        order.setDoseUnits(this.conceptService.getConcept(50));
//        order.setFrequency(this.orderService.getOrderFrequency(1));
//        order.setRoute(this.conceptService.getConcept(22));
//        order.setNumRefills(10);
//        order.setQuantity(20.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        Order expectedPreviousOrder = this.orderService.getOrder(111);
//        Assertions.assertNull(expectedPreviousOrder.getDateStopped());
//        order = (DrugOrder)this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(expectedPreviousOrder.getDateStopped(), "should populate dateStopped in previous order");
//        Assertions.assertNotNull(order.getId(), "should save discontinue order");
//        Assertions.assertEquals(expectedPreviousOrder, order.getPreviousOrder());
//        Assertions.assertNotNull(expectedPreviousOrder.getDateStopped());
//        Assertions.assertEquals(order.getDateActivated(), order.getAutoExpireDate());
//    }
//
//    @Test
//    public void saveOrder_shouldDiscontinuePreviousOrderIfItIsNotAlreadyDiscontinued() {
//        DrugOrder order = new DrugOrder();
//        order.setAction(Action.DISCONTINUE);
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setDrug(this.conceptService.getDrug(3));
//        order.setEncounter(this.encounterService.getEncounter(5));
//        order.setPatient(Context.getPatientService().getPatient(7));
//        order.setOrderer(Context.getProviderService().getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderType(this.orderService.getOrderType(1));
//        order.setDateActivated(new Date());
//        order.setDosingType(SimpleDosingInstructions.class);
//        order.setDose(500.0D);
//        order.setDoseUnits(this.conceptService.getConcept(50));
//        order.setFrequency(this.orderService.getOrderFrequency(1));
//        order.setRoute(this.conceptService.getConcept(22));
//        order.setNumRefills(10);
//        order.setQuantity(20.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        Order previousOrder = this.orderService.getOrder(111);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(previousOrder, (Date)null));
//        order.setPreviousOrder(previousOrder);
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertEquals(order.getDateActivated(), order.getAutoExpireDate());
//        Assertions.assertNotNull(previousOrder.getDateStopped(), "previous order should be discontinued");
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfConceptInPreviousOrderDoesNotMatchThisConcept() {
//        Order previousOrder = this.orderService.getOrder(7);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(previousOrder, (Date)null));
//        Order order = previousOrder.cloneForDiscontinuing();
//        order.setDateActivated(new Date());
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        Concept newConcept = this.conceptService.getConcept(5089);
//        Assertions.assertFalse(previousOrder.getConcept().equals(newConcept));
//        order.setConcept(newConcept);
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void discontinueOrder_shouldRejectAFutureDiscontinueDate() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(11, 1);
//        Patient patient = Context.getPatientService().getPatient(2);
//        CareSetting careSetting = this.orderService.getCareSetting(1);
//        Order orderToDiscontinue = (Order)this.orderService.getActiveOrders(patient, (OrderType)null, careSetting, (Date)null).get(0);
//        Encounter encounter = this.encounterService.getEncounter(3);
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.discontinueOrder(orderToDiscontinue, new Concept(), cal.getTime(), (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Discontinue date cannot be in the future"));
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailIfDiscontinueDateIsInTheFuture() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(11, 1);
//        Order orderToDiscontinue = (Order)this.orderService.getActiveOrders(Context.getPatientService().getPatient(2), (OrderType)null, this.orderService.getCareSetting(1), (Date)null).get(0);
//        Encounter encounter = this.encounterService.getEncounter(3);
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.discontinueOrder(orderToDiscontinue, "Testing", cal.getTime(), (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Discontinue date cannot be in the future"));
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfTheExistingDrugOrderMatchesTheConceptAndDrugOfTheDCOrder() {
//        DrugOrder orderToDiscontinue = (DrugOrder)this.orderService.getOrder(444);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(orderToDiscontinue, (Date)null));
//        DrugOrder order = new DrugOrder();
//        order.setDrug(orderToDiscontinue.getDrug());
//        order.setOrderType(this.orderService.getOrderTypeByName("Drug order"));
//        order.setAction(Action.DISCONTINUE);
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setPatient(orderToDiscontinue.getPatient());
//        order.setConcept(orderToDiscontinue.getConcept());
//        order.setOrderer(orderToDiscontinue.getOrderer());
//        order.setCareSetting(orderToDiscontinue.getCareSetting());
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setDateActivated(new Date());
//        order.setDosingType(SimpleDosingInstructions.class);
//        order.setDose(orderToDiscontinue.getDose());
//        order.setDoseUnits(orderToDiscontinue.getDoseUnits());
//        order.setRoute(orderToDiscontinue.getRoute());
//        order.setFrequency(orderToDiscontinue.getFrequency());
//        order.setQuantity(orderToDiscontinue.getQuantity());
//        order.setQuantityUnits(orderToDiscontinue.getQuantityUnits());
//        order.setNumRefills(orderToDiscontinue.getNumRefills());
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(orderToDiscontinue.getDateStopped(), "previous order should be discontinued");
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfTheExistingDrugOrderMatchesTheConceptAndNotDrugOfTheDCOrder() {
//        DrugOrder orderToDiscontinue = (DrugOrder)this.orderService.getOrder(5);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(orderToDiscontinue, (Date)null));
//        Drug discontinuationOrderDrug = new Drug();
//        discontinuationOrderDrug.setConcept(orderToDiscontinue.getConcept());
//        discontinuationOrderDrug = this.conceptService.saveDrug(discontinuationOrderDrug);
//        Assertions.assertNotEquals(discontinuationOrderDrug, orderToDiscontinue.getDrug());
//        Assertions.assertNotNull(orderToDiscontinue.getDrug());
//        DrugOrder order = orderToDiscontinue.cloneForRevision();
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setDrug(discontinuationOrderDrug);
//        order.setOrderReasonNonCoded("Discontinue this");
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfTheExistingDrugOrderMatchesTheConceptAndThereIsNoDrugOnThePreviousOrder() {
//        DrugOrder orderToDiscontinue = new DrugOrder();
//        orderToDiscontinue.setAction(Action.NEW);
//        orderToDiscontinue.setPatient(Context.getPatientService().getPatient(7));
//        orderToDiscontinue.setConcept(Context.getConceptService().getConcept(5497));
//        orderToDiscontinue.setCareSetting(this.orderService.getCareSetting(1));
//        orderToDiscontinue.setOrderer(this.orderService.getOrder(1).getOrderer());
//        orderToDiscontinue.setEncounter(this.encounterService.getEncounter(3));
//        orderToDiscontinue.setDateActivated(new Date());
//        orderToDiscontinue.setScheduledDate(new Date());
//        orderToDiscontinue.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        orderToDiscontinue.setEncounter(this.encounterService.getEncounter(3));
//        orderToDiscontinue.setOrderType(this.orderService.getOrderType(17));
//        orderToDiscontinue.setDrug((Drug)null);
//        orderToDiscontinue.setDosingType(FreeTextDosingInstructions.class);
//        orderToDiscontinue.setDosingInstructions("instructions");
//        orderToDiscontinue.setOrderer(this.providerService.getProvider(1));
//        orderToDiscontinue.setDosingInstructions("2 for 5 days");
//        orderToDiscontinue.setQuantity(10.0D);
//        orderToDiscontinue.setQuantityUnits(this.conceptService.getConcept(51));
//        orderToDiscontinue.setNumRefills(2);
//        this.orderService.saveOrder(orderToDiscontinue, (OrderContext)null);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(orderToDiscontinue, (Date)null));
//        DrugOrder order = orderToDiscontinue.cloneForDiscontinuing();
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderReasonNonCoded("Discontinue this");
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(orderToDiscontinue.getDateStopped(), "previous order should be discontinued");
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailForAStoppedOrder() {
//        Order orderToDiscontinue = this.orderService.getOrder(1);
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Assertions.assertNotNull(orderToDiscontinue.getDateStopped());
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.discontinueOrder(orderToDiscontinue, Context.getConceptService().getConcept(1), (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailForAVoidedOrder() {
//        Order orderToDiscontinue = this.orderService.getOrder(8);
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Assertions.assertTrue(orderToDiscontinue.getVoided());
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.discontinueOrder(orderToDiscontinue, "testing", (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void discontinueOrder_shouldFailForAnExpiredOrder() {
//        Order orderToDiscontinue = this.orderService.getOrder(6);
//        Encounter encounter = this.encounterService.getEncounter(3);
//        Assertions.assertNotNull(orderToDiscontinue.getAutoExpireDate());
//        Assertions.assertTrue(orderToDiscontinue.getAutoExpireDate().before(new Date()));
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.discontinueOrder(orderToDiscontinue, Context.getConceptService().getConcept(1), (Date)null, (Provider)null, encounter);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveOrder_shouldNotAllowEditingAnExistingOrder() {
//        DrugOrder order = (DrugOrder)this.orderService.getOrder(5);
//        UnchangeableObjectException exception = (UnchangeableObjectException)Assertions.assertThrows(UnchangeableObjectException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.edit.existing"));
//    }
//
//    @Test
//    public void getCareSettingByUuid_shouldReturnTheCareSettingWithTheSpecifiedUuid() {
//        CareSetting cs = this.orderService.getCareSettingByUuid("6f0c9a92-6f24-11e3-af88-005056821db0");
//        Assertions.assertEquals(1, cs.getId());
//    }
//
//    @Test
//    public void getCareSettingByName_shouldReturnTheCareSettingWithTheSpecifiedName() {
//        CareSetting cs = this.orderService.getCareSettingByName("INPATIENT");
//        Assertions.assertEquals(2, cs.getId());
//        cs = this.orderService.getCareSettingByName("inpatient");
//        Assertions.assertEquals(2, cs.getId());
//    }
//
//    @Test
//    public void getCareSettings_shouldReturnOnlyUnRetiredCareSettingsIfIncludeRetiredIsSetToFalse() {
//        List<CareSetting> careSettings = this.orderService.getCareSettings(false);
//        Assertions.assertEquals(2, careSettings.size());
//        Assertions.assertTrue(TestUtil.containsId(careSettings, 1));
//        Assertions.assertTrue(TestUtil.containsId(careSettings, 2));
//    }
//
//    @Test
//    public void getCareSettings_shouldReturnRetiredCareSettingsIfIncludeRetiredIsSetToTrue() {
//        CareSetting retiredCareSetting = this.orderService.getCareSetting(3);
//        Assertions.assertTrue(retiredCareSetting.getRetired());
//        List<CareSetting> careSettings = this.orderService.getCareSettings(true);
//        Assertions.assertEquals(3, careSettings.size());
//        Assertions.assertTrue(TestUtil.containsId(careSettings, retiredCareSetting.getCareSettingId()));
//    }
//
//    @Test
//    public void saveOrder_shouldNotAllowRevisingAStoppedOrder() {
//        Order originalOrder = this.orderService.getOrder(1);
//        Assertions.assertNotNull(originalOrder.getDateStopped());
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(4));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setDateActivated(new Date());
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveOrder_shouldNotAllowRevisingAVoidedOrder() {
//        Order originalOrder = this.orderService.getOrder(8);
//        Assertions.assertTrue(originalOrder.getVoided());
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(6));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setDateActivated(new Date());
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveOrder_shouldNotAllowRevisingAnExpiredOrder() {
//        Order originalOrder = this.orderService.getOrder(6);
//        Assertions.assertNotNull(originalOrder.getAutoExpireDate());
//        Assertions.assertTrue(originalOrder.getAutoExpireDate().before(new Date()));
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(6));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setDateActivated(new Date());
//        revisedOrder.setAutoExpireDate(new Date());
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveOrder_shouldNotAllowRevisingAnOrderWithNoPreviousOrder() {
//        Order originalOrder = this.orderService.getOrder(111);
//        Assertions.assertTrue(originalOrder.isActive());
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(5));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setPreviousOrder((Order)null);
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setDateActivated(new Date());
//        MissingRequiredPropertyException exception = (MissingRequiredPropertyException)Assertions.assertThrows(MissingRequiredPropertyException.class, () -> {
//            this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.previous.required")));
//    }
//
//    @Test
//    public void saveOrder_shouldSaveARevisedOrder() {
//        Order originalOrder = this.orderService.getOrder(111);
//        Assertions.assertTrue(originalOrder.isActive());
//        Patient patient = originalOrder.getPatient();
//        List<Order> originalActiveOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        int originalOrderCount = originalActiveOrders.size();
//        Assertions.assertTrue(originalActiveOrders.contains(originalOrder));
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(5));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setDateActivated(new Date());
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setEncounter(this.encounterService.getEncounter(3));
//        this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        List<Order> activeOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(originalOrderCount, activeOrders.size());
//        Assertions.assertEquals(revisedOrder.getDateActivated(), DateUtils.addSeconds(originalOrder.getDateStopped(), 1));
//        Assertions.assertFalse(originalOrder.isActive());
//    }
//
//    @Test
//    public void updateOrderFulfillerStatus_shouldEditFulfillerStatusInOrder() {
//        Order originalOrder = this.orderService.getOrder(111);
//        String commentText = "We got the new order";
//        Assertions.assertNotEquals(originalOrder.getFulfillerStatus(), FulfillerStatus.IN_PROGRESS);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, FulfillerStatus.IN_PROGRESS, commentText);
//        Context.flushSession();
//        Order updatedOrder = this.orderService.getOrder(111);
//        Assertions.assertEquals(FulfillerStatus.IN_PROGRESS, updatedOrder.getFulfillerStatus());
//        Assertions.assertEquals(commentText, updatedOrder.getFulfillerComment());
//    }
//
//    @Test
//    public void updateOrderFulfillerStatus_shouldEditFulfillerStatusWithAccessionNumberInOrder() {
//        Order originalOrder = this.orderService.getOrder(111);
//        String commentText = "We got the new order";
//        String accessionNumber = "12345";
//        Assertions.assertNotEquals(originalOrder.getAccessionNumber(), accessionNumber);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, FulfillerStatus.IN_PROGRESS, commentText, accessionNumber);
//        Context.flushSession();
//        Order updatedOrder = this.orderService.getOrder(111);
//        Assertions.assertEquals(FulfillerStatus.IN_PROGRESS, updatedOrder.getFulfillerStatus());
//        Assertions.assertEquals(commentText, updatedOrder.getFulfillerComment());
//        Assertions.assertEquals(accessionNumber, updatedOrder.getAccessionNumber());
//    }
//
//    @Test
//    public void updateOrderFulfillerStatus_shouldNotUpdateFulfillerStatusNullParameters() {
//        Order originalOrder = this.orderService.getOrder(111);
//        String commentText = "We got the new order";
//        String accessionNumber = "12345";
//        Assertions.assertNotEquals(originalOrder.getAccessionNumber(), accessionNumber);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, FulfillerStatus.IN_PROGRESS, commentText, accessionNumber);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, (FulfillerStatus)null, (String)null, (String)null);
//        Context.flushSession();
//        Order updatedOrder = this.orderService.getOrder(111);
//        Assertions.assertEquals(FulfillerStatus.IN_PROGRESS, updatedOrder.getFulfillerStatus());
//        Assertions.assertEquals(commentText, updatedOrder.getFulfillerComment());
//        Assertions.assertEquals(accessionNumber, updatedOrder.getAccessionNumber());
//    }
//
//    @Test
//    public void updateOrderFulfillerStatus_shouldUpdateFulfillerStatusWithEmptyStrings() {
//        Order originalOrder = this.orderService.getOrder(111);
//        String commentText = "We got the new order";
//        String accessionNumber = "12345";
//        Assertions.assertNotEquals(originalOrder.getAccessionNumber(), accessionNumber);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, FulfillerStatus.IN_PROGRESS, commentText, accessionNumber);
//        this.orderService.updateOrderFulfillerStatus(originalOrder, (FulfillerStatus)null, "", "");
//        Context.flushSession();
//        Order updatedOrder = this.orderService.getOrder(111);
//        Assertions.assertEquals(FulfillerStatus.IN_PROGRESS, updatedOrder.getFulfillerStatus());
//        Assertions.assertEquals("", updatedOrder.getFulfillerComment());
//        Assertions.assertEquals("", updatedOrder.getAccessionNumber());
//    }
//
//    @Test
//    public void saveOrder_shouldSaveARevisedOrderForAScheduledOrderWhichIsNotStarted() {
//        Order originalOrder = new Order();
//        originalOrder.setAction(Action.NEW);
//        originalOrder.setPatient(Context.getPatientService().getPatient(7));
//        originalOrder.setConcept(Context.getConceptService().getConcept(5497));
//        originalOrder.setCareSetting(this.orderService.getCareSetting(1));
//        originalOrder.setOrderer(this.orderService.getOrder(1).getOrderer());
//        originalOrder.setEncounter(this.encounterService.getEncounter(3));
//        originalOrder.setOrderType(this.orderService.getOrderType(17));
//        originalOrder.setDateActivated(new Date());
//        originalOrder.setScheduledDate(DateUtils.addMonths(new Date(), 2));
//        originalOrder.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        originalOrder = this.orderService.saveOrder(originalOrder, (OrderContext)null);
//        Assertions.assertTrue(originalOrder.isActive());
//        Patient patient = originalOrder.getPatient();
//        List<Order> originalActiveOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        int originalOrderCount = originalActiveOrders.size();
//        Assertions.assertTrue(originalActiveOrders.contains(originalOrder));
//        Order revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setEncounter(this.encounterService.getEncounter(5));
//        revisedOrder.setInstructions("Take after a meal");
//        revisedOrder.setDateActivated(new Date());
//        revisedOrder.setOrderer(this.providerService.getProvider(1));
//        revisedOrder.setEncounter(this.encounterService.getEncounter(3));
//        this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        List<Order> activeOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        Assertions.assertEquals(originalOrderCount, activeOrders.size());
//        Assertions.assertEquals(revisedOrder.getDateActivated(), DateUtils.addSeconds(originalOrder.getDateStopped(), 1));
//        Assertions.assertFalse(activeOrders.contains(originalOrder));
//        Assertions.assertFalse(originalOrder.isActive());
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldGetNonRetiredFrequenciesWithNamesMatchingThePhraseIfIncludeRetiredIsFalse() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies("once", Locale.US, false, false);
//        Assertions.assertEquals(2, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 100));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 102));
//        orderFrequencies = this.orderService.getOrderFrequencies("nce", Locale.US, false, false);
//        Assertions.assertEquals(2, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 100));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 102));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldIncludeRetiredFrequenciesIfIncludeRetiredIsSetToTrue() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies("ce", Locale.US, false, true);
//        Assertions.assertEquals(4, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 100));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 101));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 102));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 103));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldGetFrequenciesWithNamesThatMatchThePhraseAndLocalesIfExactLocaleIsFalse() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies("ce", Locale.US, false, false);
//        Assertions.assertEquals(3, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 100));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 101));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 102));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldGetFrequenciesWithNamesThatMatchThePhraseAndLocaleIfExactLocaleIsTrue() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies("ce", Locale.US, true, false);
//        Assertions.assertEquals(1, orderFrequencies.size());
//        Assertions.assertEquals(102, ((OrderFrequency)orderFrequencies.get(0)).getOrderFrequencyId());
//        orderFrequencies = this.orderService.getOrderFrequencies("ce", Locale.ENGLISH, true, false);
//        Assertions.assertEquals(2, orderFrequencies.size());
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 100));
//        Assertions.assertTrue(TestUtil.containsId(orderFrequencies, 101));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldReturnUniqueFrequencies() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        String searchPhrase = "once";
//        Locale locale = Locale.ENGLISH;
//        List<OrderFrequency> orderFrequencies = this.orderService.getOrderFrequencies("once", locale, true, false);
//        Assertions.assertEquals(1, orderFrequencies.size());
//        OrderFrequency expectedOrderFrequency = this.orderService.getOrderFrequency(100);
//        Assertions.assertEquals(expectedOrderFrequency, orderFrequencies.get(0));
//        Concept frequencyConcept = expectedOrderFrequency.getConcept();
//        String newConceptName = "once A Day";
//        frequencyConcept.addName(new ConceptName("once A Day", locale));
//        frequencyConcept.addDescription(new ConceptDescription("some description", (Locale)null));
//        this.conceptService.saveConcept(frequencyConcept);
//        orderFrequencies = this.orderService.getOrderFrequencies("once", locale, true, false);
//        Assertions.assertEquals(1, orderFrequencies.size());
//        Assertions.assertEquals(expectedOrderFrequency, orderFrequencies.get(0));
//    }
//
//    @Test
//    public void getOrderFrequencies_shouldRejectANullSearchPhrase() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getOrderFrequencies((String)null, Locale.ENGLISH, false, false);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("searchPhrase is required"));
//    }
//
//    @Test
//    public void retireOrderFrequency_shouldRetireGivenOrderFrequency() {
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(1);
//        Assertions.assertNotNull(orderFrequency);
//        Assertions.assertFalse(orderFrequency.getRetired());
//        Assertions.assertNull(orderFrequency.getRetireReason());
//        Assertions.assertNull(orderFrequency.getDateRetired());
//        this.orderService.retireOrderFrequency(orderFrequency, "retire reason");
//        orderFrequency = this.orderService.getOrderFrequency(1);
//        Assertions.assertNotNull(orderFrequency);
//        Assertions.assertTrue(orderFrequency.getRetired());
//        Assertions.assertEquals("retire reason", orderFrequency.getRetireReason());
//        Assertions.assertNotNull(orderFrequency.getDateRetired());
//        Assertions.assertEquals(3, this.orderService.getOrderFrequencies(true).size());
//    }
//
//    @Test
//    public void unretireOrderFrequency_shouldUnretireGivenOrderFrequency() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(103);
//        Assertions.assertNotNull(orderFrequency);
//        Assertions.assertTrue(orderFrequency.getRetired());
//        Assertions.assertNotNull(orderFrequency.getRetireReason());
//        Assertions.assertNotNull(orderFrequency.getDateRetired());
//        this.orderService.unretireOrderFrequency(orderFrequency);
//        orderFrequency = this.orderService.getOrderFrequency(103);
//        Assertions.assertNotNull(orderFrequency);
//        Assertions.assertFalse(orderFrequency.getRetired());
//        Assertions.assertNull(orderFrequency.getRetireReason());
//        Assertions.assertNull(orderFrequency.getDateRetired());
//        Assertions.assertEquals(7, this.orderService.getOrderFrequencies(true).size());
//    }
//
//    @Test
//    public void purgeOrderFrequency_shouldDeleteGivenOrderFrequency() {
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(3);
//        Assertions.assertNotNull(orderFrequency);
//        this.orderService.purgeOrderFrequency(orderFrequency);
//        orderFrequency = this.orderService.getOrderFrequency(3);
//        Assertions.assertNull(orderFrequency);
//        Assertions.assertEquals(2, this.orderService.getOrderFrequencies(true).size());
//    }
//
//    @Test
//    public void saveOrderFrequency_shouldAddANewOrderFrequencyToTheDatabase() {
//        Concept concept = new Concept();
//        concept.addName(new ConceptName("new name", Context.getLocale()));
//        concept.addDescription(new ConceptDescription("some description", (Locale)null));
//        concept.setDatatype(new ConceptDatatype(1));
//        concept.setConceptClass(this.conceptService.getConceptClassByName("Frequency"));
//        concept = this.conceptService.saveConcept(concept);
//        Integer originalSize = this.orderService.getOrderFrequencies(true).size();
//        OrderFrequency orderFrequency = new OrderFrequency();
//        orderFrequency.setConcept(concept);
//        orderFrequency.setFrequencyPerDay(2.0D);
//        orderFrequency = this.orderService.saveOrderFrequency(orderFrequency);
//        Assertions.assertNotNull(orderFrequency.getId());
//        Assertions.assertNotNull(orderFrequency.getUuid());
//        Assertions.assertNotNull(orderFrequency.getCreator());
//        Assertions.assertNotNull(orderFrequency.getDateCreated());
//        Assertions.assertEquals(originalSize + 1, this.orderService.getOrderFrequencies(true).size());
//    }
//
//    @Test
//    public void saveOrderFrequency_shouldEditAnExistingOrderFrequencyThatIsNotInUse() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrderFrequencies.xml");
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(100);
//        Assertions.assertNotNull(orderFrequency);
//        orderFrequency.setFrequencyPerDay(4.0D);
//        this.orderService.saveOrderFrequency(orderFrequency);
//    }
//
//    @Test
//    public void saveOrderFrequency_shouldNotAllowEditingAnExistingOrderFrequencyThatIsInUse() {
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(1);
//        Assertions.assertNotNull(orderFrequency);
//        orderFrequency.setFrequencyPerDay(4.0D);
//        CannotUpdateObjectInUseException exception = (CannotUpdateObjectInUseException)Assertions.assertThrows(CannotUpdateObjectInUseException.class, () -> {
//            this.orderService.saveOrderFrequency(orderFrequency);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.frequency.cannot.edit"));
//    }
//
//    @Test
//    public void purgeOrderFrequency_shouldNotAllowDeletingAnOrderFrequencyThatIsInUse() {
//        OrderFrequency orderFrequency = this.orderService.getOrderFrequency(1);
//        Assertions.assertNotNull(orderFrequency);
//        CannotDeleteObjectInUseException exception = (CannotDeleteObjectInUseException)Assertions.assertThrows(CannotDeleteObjectInUseException.class, () -> {
//            this.orderService.purgeOrderFrequency(orderFrequency);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.frequency.cannot.delete")));
//    }
//
//    @Test
//    public void saveOrderWithScheduledDate_shouldAddANewOrderWithScheduledDateToTheDatabase() {
//        Date scheduledDate = new Date();
//        Order order = new Order();
//        order.setAction(Action.NEW);
//        order.setPatient(Context.getPatientService().getPatient(7));
//        order.setConcept(Context.getConceptService().getConcept(5497));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setOrderer(this.orderService.getOrder(1).getOrderer());
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        order.setScheduledDate(scheduledDate);
//        order.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setOrderType(this.orderService.getOrderType(17));
//        order = this.orderService.saveOrder(order, (OrderContext)null);
//        Order newOrder = this.orderService.getOrder(order.getOrderId());
//        Assertions.assertNotNull(order);
//        Assertions.assertEquals(DateUtil.truncateToSeconds(scheduledDate), order.getScheduledDate());
//        Assertions.assertNotNull(newOrder);
//        Assertions.assertEquals(DateUtil.truncateToSeconds(scheduledDate), newOrder.getScheduledDate());
//    }
//
//    @Test
//    public void saveOrder_shouldSetOrderNumberSpecifiedInTheContextIfSpecified() {
//        GlobalProperty gp = new GlobalProperty("order.orderNumberGeneratorBeanId", "orderEntry.OrderNumberGenerator");
//        Context.getAdministrationService().saveGlobalProperty(gp);
//        Order order = new TestOrder();
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setPatient(this.patientService.getPatient(7));
//        order.setConcept(this.conceptService.getConcept(5497));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setOrderType(this.orderService.getOrderType(2));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        OrderContext orderCtxt = new OrderContext();
//        String expectedOrderNumber = "Testing";
//        orderCtxt.setAttribute("nextOrderNumber", "Testing");
//        Order order2 = this.orderService.saveOrder(order, orderCtxt);
//        Assertions.assertEquals("Testing", order2.getOrderNumber());
//    }
//
//    @Test
//    public void saveOrder_shouldSetTheOrderNumberReturnedByTheConfiguredGenerator() {
//        GlobalProperty gp = new GlobalProperty("order.orderNumberGeneratorBeanId", "orderEntry.OrderNumberGenerator");
//        Context.getAdministrationService().saveGlobalProperty(gp);
//        Order order = new TestOrder();
//        order.setPatient(this.patientService.getPatient(7));
//        order.setConcept(this.conceptService.getConcept(5497));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setOrderType(this.orderService.getOrderType(2));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        Order order2 = this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertTrue(order2.getOrderNumber().startsWith("TEST-PREFIX-"));
//    }
//
//    @Test
//    @Disabled("Ignored because it fails after removal of deprecated methods TRUNK-4772")
//    public void saveOrder_shouldFailForRevisionOrderIfAnActiveDrugOrderForTheSameConceptAndCareSettingsExists() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept aspirin = this.conceptService.getConcept(88);
//        DrugOrder firstOrder = new DrugOrder();
//        firstOrder.setPatient(patient);
//        firstOrder.setConcept(aspirin);
//        firstOrder.setEncounter(this.encounterService.getEncounter(6));
//        firstOrder.setOrderer(this.providerService.getProvider(1));
//        firstOrder.setCareSetting(this.orderService.getCareSetting(2));
//        firstOrder.setDrug(this.conceptService.getDrug(3));
//        firstOrder.setDateActivated(new Date());
//        firstOrder.setAutoExpireDate(DateUtils.addDays(new Date(), 10));
//        firstOrder.setDosingType(FreeTextDosingInstructions.class);
//        firstOrder.setDosingInstructions("2 for 5 days");
//        firstOrder.setQuantity(10.0D);
//        firstOrder.setQuantityUnits(this.conceptService.getConcept(51));
//        firstOrder.setNumRefills(0);
//        this.orderService.saveOrder(firstOrder, (OrderContext)null);
//        DrugOrder secondOrder = new DrugOrder();
//        secondOrder.setPatient(firstOrder.getPatient());
//        secondOrder.setConcept(firstOrder.getConcept());
//        secondOrder.setEncounter(this.encounterService.getEncounter(6));
//        secondOrder.setOrderer(this.providerService.getProvider(1));
//        secondOrder.setCareSetting(firstOrder.getCareSetting());
//        secondOrder.setDrug(this.conceptService.getDrug(3));
//        secondOrder.setDateActivated(new Date());
//        secondOrder.setScheduledDate(DateUtils.addDays(firstOrder.getEffectiveStopDate(), 1));
//        secondOrder.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        secondOrder.setDosingType(FreeTextDosingInstructions.class);
//        secondOrder.setDosingInstructions("2 for 5 days");
//        secondOrder.setQuantity(10.0D);
//        secondOrder.setQuantityUnits(this.conceptService.getConcept(51));
//        secondOrder.setNumRefills(0);
//        this.orderService.saveOrder(secondOrder, (OrderContext)null);
//        DrugOrder revision = secondOrder.cloneForRevision();
//        revision.setScheduledDate(DateUtils.addDays(firstOrder.getEffectiveStartDate(), 2));
//        revision.setEncounter(this.encounterService.getEncounter(6));
//        revision.setOrderer(this.providerService.getProvider(1));
//        APIException exception = (APIException)Assertions.assertThrows(APIException.class, () -> {
//            this.orderService.saveOrder(revision, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.have.more.than.one"));
//    }
//
//    @Test
//    @Disabled("Ignored because it fails after removal of deprecated methods TRUNK-4772")
//    public void saveOrder_shouldPassForRevisionOrderIfAnActiveTestOrderForTheSameConceptAndCareSettingsExists() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept cd4Count = this.conceptService.getConcept(5497);
//        TestOrder activeOrder = new TestOrder();
//        activeOrder.setPatient(patient);
//        activeOrder.setConcept(cd4Count);
//        activeOrder.setEncounter(this.encounterService.getEncounter(6));
//        activeOrder.setOrderer(this.providerService.getProvider(1));
//        activeOrder.setCareSetting(this.orderService.getCareSetting(2));
//        activeOrder.setDateActivated(new Date());
//        activeOrder.setAutoExpireDate(DateUtils.addDays(new Date(), 10));
//        this.orderService.saveOrder(activeOrder, (OrderContext)null);
//        TestOrder secondOrder = new TestOrder();
//        secondOrder.setPatient(activeOrder.getPatient());
//        secondOrder.setConcept(activeOrder.getConcept());
//        secondOrder.setEncounter(this.encounterService.getEncounter(6));
//        secondOrder.setOrderer(this.providerService.getProvider(1));
//        secondOrder.setCareSetting(activeOrder.getCareSetting());
//        secondOrder.setDateActivated(new Date());
//        secondOrder.setScheduledDate(DateUtils.addDays(activeOrder.getEffectiveStopDate(), 1));
//        secondOrder.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        this.orderService.saveOrder(secondOrder, (OrderContext)null);
//        TestOrder revision = secondOrder.cloneForRevision();
//        revision.setScheduledDate(DateUtils.addDays(activeOrder.getEffectiveStartDate(), 2));
//        revision.setEncounter(this.encounterService.getEncounter(6));
//        revision.setOrderer(this.providerService.getProvider(1));
//        Order savedSecondOrder = this.orderService.saveOrder(revision, (OrderContext)null);
//        Assertions.assertNotNull(this.orderService.getOrder(savedSecondOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfAnActiveDrugOrderForTheSameConceptAndCareSettingExists() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept triomuneThirty = this.conceptService.getConcept(792);
//        DrugOrder duplicateOrder = (DrugOrder)this.orderService.getOrder(3);
//        Assertions.assertTrue(duplicateOrder.isActive());
//        Assertions.assertEquals(triomuneThirty, duplicateOrder.getConcept());
//        DrugOrder drugOrder = new DrugOrder();
//        drugOrder.setPatient(patient);
//        drugOrder.setCareSetting(this.orderService.getCareSetting(1));
//        drugOrder.setConcept(triomuneThirty);
//        drugOrder.setEncounter(this.encounterService.getEncounter(6));
//        drugOrder.setOrderer(this.providerService.getProvider(1));
//        drugOrder.setCareSetting(duplicateOrder.getCareSetting());
//        drugOrder.setDrug(duplicateOrder.getDrug());
//        drugOrder.setDose(duplicateOrder.getDose());
//        drugOrder.setDoseUnits(duplicateOrder.getDoseUnits());
//        drugOrder.setRoute(duplicateOrder.getRoute());
//        drugOrder.setFrequency(duplicateOrder.getFrequency());
//        drugOrder.setQuantity(duplicateOrder.getQuantity());
//        drugOrder.setQuantityUnits(duplicateOrder.getQuantityUnits());
//        drugOrder.setNumRefills(duplicateOrder.getNumRefills());
//        AmbiguousOrderException exception = (AmbiguousOrderException)Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.have.more.than.one"));
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnActiveTestOrderForTheSameConceptAndCareSettingExists() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept cd4Count = this.conceptService.getConcept(5497);
//        TestOrder duplicateOrder = (TestOrder)this.orderService.getOrder(7);
//        Assertions.assertTrue(duplicateOrder.isActive());
//        Assertions.assertEquals(cd4Count, duplicateOrder.getConcept());
//        Order order = new TestOrder();
//        order.setPatient(patient);
//        order.setCareSetting(this.orderService.getCareSetting(2));
//        order.setConcept(cd4Count);
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(duplicateOrder.getCareSetting());
//        Order savedOrder = this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(this.orderService.getOrder(savedOrder.getOrderId()));
//    }
//
//    @Test
//    @Disabled("Ignored because it fails after removal of deprecated methods TRUNK-4772")
//    public void saveOrder_shouldSaveRevisionOrderScheduledOnDateNotOverlappingWithAnActiveOrderForTheSameConceptAndCareSetting() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept cd4Count = this.conceptService.getConcept(5497);
//        TestOrder activeOrder = new TestOrder();
//        activeOrder.setPatient(patient);
//        activeOrder.setConcept(cd4Count);
//        activeOrder.setEncounter(this.encounterService.getEncounter(6));
//        activeOrder.setOrderer(this.providerService.getProvider(1));
//        activeOrder.setCareSetting(this.orderService.getCareSetting(2));
//        activeOrder.setDateActivated(new Date());
//        activeOrder.setAutoExpireDate(DateUtils.addDays(new Date(), 10));
//        this.orderService.saveOrder(activeOrder, (OrderContext)null);
//        TestOrder secondOrder = new TestOrder();
//        secondOrder.setPatient(activeOrder.getPatient());
//        secondOrder.setConcept(activeOrder.getConcept());
//        secondOrder.setEncounter(this.encounterService.getEncounter(6));
//        secondOrder.setOrderer(this.providerService.getProvider(1));
//        secondOrder.setCareSetting(activeOrder.getCareSetting());
//        secondOrder.setDateActivated(new Date());
//        secondOrder.setScheduledDate(DateUtils.addDays(activeOrder.getEffectiveStopDate(), 1));
//        secondOrder.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        this.orderService.saveOrder(secondOrder, (OrderContext)null);
//        TestOrder revision = secondOrder.cloneForRevision();
//        revision.setScheduledDate(DateUtils.addDays(activeOrder.getEffectiveStopDate(), 2));
//        revision.setEncounter(this.encounterService.getEncounter(6));
//        revision.setOrderer(this.providerService.getProvider(1));
//        Order savedRevisionOrder = this.orderService.saveOrder(revision, (OrderContext)null);
//        Assertions.assertNotNull(this.orderService.getOrder(savedRevisionOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnActiveDrugOrderForTheSameConceptAndCareSettingButDifferentFormulationExists() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-drugOrdersWithSameConceptAndDifferentFormAndStrength.xml");
//        Patient patient = this.patientService.getPatient(2);
//        DrugOrder existingOrder = (DrugOrder)this.orderService.getOrder(1000);
//        Assertions.assertTrue(existingOrder.isActive());
//        DrugOrder order = new DrugOrder();
//        order.setPatient(patient);
//        order.setConcept(existingOrder.getConcept());
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(existingOrder.getCareSetting());
//        order.setDrug(this.conceptService.getDrug(3001));
//        order.setDosingType(FreeTextDosingInstructions.class);
//        order.setDosingInstructions("2 for 5 days");
//        order.setQuantity(10.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        order.setNumRefills(2);
//        Order savedDrugOrder = this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(this.orderService.getOrder(savedDrugOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveOrder_shouldThrowAmbiguousOrderExceptionIfAnActiveDrugOrderForTheSameDrugFormulationExists() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-drugOrdersWithSameConceptAndDifferentFormAndStrength.xml");
//        Patient patient = this.patientService.getPatient(2);
//        DrugOrder existingOrder = (DrugOrder)this.orderService.getOrder(1000);
//        Assertions.assertTrue(existingOrder.isActive());
//        DrugOrder order = new DrugOrder();
//        order.setPatient(patient);
//        order.setDrug(existingOrder.getDrug());
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(existingOrder.getCareSetting());
//        order.setDosingType(FreeTextDosingInstructions.class);
//        order.setDosingInstructions("2 for 5 days");
//        order.setQuantity(10.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        order.setNumRefills(2);
//        AmbiguousOrderException exception = (AmbiguousOrderException)Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.have.more.than.one"));
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnActiveOrderForTheSameConceptExistsInADifferentCareSetting() {
//        Patient patient = this.patientService.getPatient(2);
//        Concept cd4Count = this.conceptService.getConcept(5497);
//        TestOrder duplicateOrder = (TestOrder)this.orderService.getOrder(7);
//        CareSetting inpatient = this.orderService.getCareSetting(2);
//        Assertions.assertNotEquals(inpatient, duplicateOrder.getCareSetting());
//        Assertions.assertTrue(duplicateOrder.isActive());
//        Assertions.assertEquals(cd4Count, duplicateOrder.getConcept());
//        int initialActiveOrderCount = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null).size();
//        TestOrder order = new TestOrder();
//        order.setPatient(patient);
//        order.setCareSetting(this.orderService.getCareSetting(2));
//        order.setConcept(cd4Count);
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(inpatient);
//        this.orderService.saveOrder(order, (OrderContext)null);
//        List<Order> activeOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        ++initialActiveOrderCount;
//        Assertions.assertEquals(initialActiveOrderCount, activeOrders.size());
//    }
//
//    @Test
//    public void saveOrder_shouldRollTheAutoExpireDateToTheEndOfTheDayIfItHasNoTimeComponent() throws ParseException {
//        Order order = new TestOrder();
//        order.setPatient(this.patientService.getPatient(2));
//        order.setCareSetting(this.orderService.getCareSetting(2));
//        order.setConcept(this.conceptService.getConcept(5089));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
//        order.setDateActivated(dateformat.parse("14/08/2014"));
//        order.setAutoExpireDate(dateformat.parse("18/08/2014"));
//        this.orderService.saveOrder(order, (OrderContext)null);
//        dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
//        Assertions.assertEquals(dateformat.parse("18/08/2014 23:59:59.000"), order.getAutoExpireDate());
//    }
//
//    @Test
//    public void saveOrder_shouldNotChangeTheAutoExpireDateIfItHasATimeComponent() throws ParseException {
//        Order order = new TestOrder();
//        order.setPatient(this.patientService.getPatient(2));
//        order.setCareSetting(this.orderService.getCareSetting(2));
//        order.setConcept(this.conceptService.getConcept(5089));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setDateActivated(new Date());
//        DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        order.setDateActivated(dateformat.parse("14/08/2014 10:00:00"));
//        Date autoExpireDate = dateformat.parse("18/08/2014 10:00:00");
//        order.setAutoExpireDate(autoExpireDate);
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertEquals(autoExpireDate, order.getAutoExpireDate());
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnActiveDrugOrderForTheSameDrugFormulationExistsBeyondSchedule() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-DrugOrders.xml");
//        Patient patient = this.patientService.getPatient(2);
//        DrugOrder existingOrder = (DrugOrder)this.orderService.getOrder(2000);
//        int initialActiveOrderCount = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null).size();
//        DrugOrder order = new DrugOrder();
//        order.setPatient(patient);
//        order.setDrug(existingOrder.getDrug());
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(existingOrder.getCareSetting());
//        order.setDosingType(FreeTextDosingInstructions.class);
//        order.setDosingInstructions("2 for 10 days");
//        order.setQuantity(10.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        order.setNumRefills(2);
//        order.setUrgency(Urgency.ON_SCHEDULED_DATE);
//        order.setScheduledDate(DateUtils.addDays(existingOrder.getDateStopped(), 1));
//        this.orderService.saveOrder(order, (OrderContext)null);
//        List<Order> activeOrders = this.orderService.getActiveOrders(patient, (OrderType)null, (CareSetting)null, (Date)null);
//        ++initialActiveOrderCount;
//        Assertions.assertEquals(initialActiveOrderCount, activeOrders.size());
//    }
//
//    @Test
//    public void getOrderType_shouldFindOrderTypeObjectGivenValidId() {
//        Assertions.assertEquals("Drug order", this.orderService.getOrderType(1).getName());
//    }
//
//    @Test
//    public void getOrderType_shouldReturnNullIfNoOrderTypeObjectFoundWithGivenId() {
//        OrderType orderType = this.orderService.getOrderType(1000);
//        Assertions.assertNull(orderType);
//    }
//
//    @Test
//    public void getOrderTypeByUuid_shouldFindOrderTypeObjectGivenValidUuid() {
//        OrderType orderType = this.orderService.getOrderTypeByUuid("131168f4-15f5-102d-96e4-000c29c2a5d7");
//        Assertions.assertEquals("Drug order", orderType.getName());
//    }
//
//    @Test
//    public void getOrderTypeByUuid_shouldReturnNullIfNoOrderTypeObjectFoundWithGivenUuid() {
//        Assertions.assertNull(this.orderService.getOrderTypeByUuid("some random uuid"));
//    }
//
//    @Test
//    public void getOrderTypes_shouldGetAllOrderTypesIfIncludeRetiredIsSetToTrue() {
//        Assertions.assertEquals(14, this.orderService.getOrderTypes(true).size());
//    }
//
//    @Test
//    public void getOrderTypes_shouldGetAllNonRetiredOrderTypesIfIncludeRetiredIsSetToFalse() {
//        Assertions.assertEquals(11, this.orderService.getOrderTypes(false).size());
//    }
//
//    @Test
//    public void getOrderTypeByName_shouldReturnTheOrderTypeThatMatchesTheSpecifiedName() {
//        OrderType orderType = this.orderService.getOrderTypeByName("Drug order");
//        Assertions.assertEquals("131168f4-15f5-102d-96e4-000c29c2a5d7", orderType.getUuid());
//    }
//
//    @Test
//    public void getOrders_shouldFailIfPatientIsNull() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getOrders((Patient)null, (CareSetting)null, (OrderType)null, false);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Patient is required"));
//    }
//
//    @Test
//    public void getOrders_shouldFailIfCareSettingIsNull() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getOrders(new Patient(), (CareSetting)null, (OrderType)null, false);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("CareSetting is required"));
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersThatMatchAllTheArguments() {
//        Patient patient = this.patientService.getPatient(2);
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        OrderType testOrderType = this.orderService.getOrderType(2);
//        List<Order> testOrders = this.orderService.getOrders(patient, outPatient, testOrderType, false);
//        Assertions.assertEquals(3, testOrders.size());
//        TestUtil.containsId(testOrders, 6);
//        TestUtil.containsId(testOrders, 7);
//        TestUtil.containsId(testOrders, 9);
//        OrderType drugOrderType = this.orderService.getOrderType(1);
//        List<Order> drugOrders = this.orderService.getOrders(patient, outPatient, drugOrderType, false);
//        Assertions.assertEquals(5, drugOrders.size());
//        TestUtil.containsId(drugOrders, 2);
//        TestUtil.containsId(drugOrders, 3);
//        TestUtil.containsId(drugOrders, 44);
//        TestUtil.containsId(drugOrders, 444);
//        TestUtil.containsId(drugOrders, 5);
//        CareSetting inPatient = this.orderService.getCareSetting(2);
//        List<Order> inPatientDrugOrders = this.orderService.getOrders(patient, inPatient, drugOrderType, false);
//        Assertions.assertEquals(222, ((Order)inPatientDrugOrders.get(0)).getOrderId());
//    }
//
//    @Test
//    public void getOrders_shouldGetAllUnvoidedMatchesIfIncludeVoidedIsSetToFalse() {
//        Patient patient = this.patientService.getPatient(2);
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        OrderType testOrderType = this.orderService.getOrderType(2);
//        Assertions.assertEquals(3, this.orderService.getOrders(patient, outPatient, testOrderType, false).size());
//    }
//
//    @Test
//    public void getOrders_shouldIncludeVoidedMatchesIfIncludeVoidedIsSetToTrue() {
//        Patient patient = this.patientService.getPatient(2);
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        OrderType testOrderType = this.orderService.getOrderType(2);
//        Assertions.assertEquals(4, this.orderService.getOrders(patient, outPatient, testOrderType, true).size());
//    }
//
//    @Test
//    public void getOrders_shouldIncludeOrdersForSubTypesIfOrderTypeIsSpecified() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-otherOrders.xml");
//        Patient patient = this.patientService.getPatient(2);
//        OrderType testOrderType = this.orderService.getOrderType(2);
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        List<Order> orders = this.orderService.getOrders(patient, outPatient, testOrderType, false);
//        Assertions.assertEquals(7, orders.size());
//        Order[] expectedOrder1 = new Order[]{this.orderService.getOrder(6), this.orderService.getOrder(7), this.orderService.getOrder(9), this.orderService.getOrder(101), this.orderService.getOrder(102), this.orderService.getOrder(103), this.orderService.getOrder(104)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrder1));
//        OrderType labTestOrderType = this.orderService.getOrderType(7);
//        orders = this.orderService.getOrders(patient, outPatient, labTestOrderType, false);
//        Assertions.assertEquals(3, this.orderService.getOrders(patient, outPatient, labTestOrderType, false).size());
//        Order[] expectedOrder2 = new Order[]{this.orderService.getOrder(101), this.orderService.getOrder(103), this.orderService.getOrder(104)};
//        MatcherAssert.assertThat(orders, Matchers.hasItems(expectedOrder2));
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByPatient() {
//        Patient patient = this.patientService.getPatient(2);
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setPatient(patient).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(11, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetStoppedOrders() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setIsStopped(true).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(4, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertNotNull(order.getDateStopped());
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldReturnOrdersAutoExpiredBeforeDate() {
//        Date autoExpireOnOrBeforeDate = (new GregorianCalendar(2008, 9, 30)).getTime();
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setAutoExpireOnOrBeforeDate(autoExpireOnOrBeforeDate).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(4, orders.size());
//        Iterator var4 = orders.iterator();
//
//        while(var4.hasNext()) {
//            Order order = (Order)var4.next();
//            Assertions.assertNotNull(order.getAutoExpireDate());
//            Assertions.assertTrue(autoExpireOnOrBeforeDate.after(order.getAutoExpireDate()));
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldReturnOnlyCanceledOrAutoExpiredOrdersBeforeDate() {
//        Date canceledOrExpiredOnOrBeforeDate = (new GregorianCalendar(2008, 9, 30)).getTime();
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setCanceledOrExpiredOnOrBeforeDate(canceledOrExpiredOnOrBeforeDate).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(7, orders.size());
//        Iterator var4 = orders.iterator();
//
//        while(var4.hasNext()) {
//            Order order = (Order)var4.next();
//            Assertions.assertTrue(order.getDateStopped() != null && order.getDateStopped().before(canceledOrExpiredOnOrBeforeDate) || order.getAutoExpireDate() != null && order.getAutoExpireDate().before(canceledOrExpiredOnOrBeforeDate));
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldNotReturnCanceledOrAutoExpiredOrders() {
//        Date today = Calendar.getInstance().getTime();
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setExcludeCanceledAndExpired(true).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(6, orders.size());
//        Iterator var4 = orders.iterator();
//
//        while(var4.hasNext()) {
//            Order order = (Order)var4.next();
//            Assertions.assertTrue((order.getDateStopped() == null || order.getDateStopped() != null && order.getDateStopped().after(today)) && (order.getAutoExpireDate() == null || order.getAutoExpireDate() != null && order.getAutoExpireDate().after(today)));
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldreturnOrdersWithFulfillerStatusCompleted() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setFulfillerStatus(FulfillerStatus.valueOf("COMPLETED")).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(1, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertEquals(FulfillerStatus.COMPLETED, order.getFulfillerStatus());
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldReturnOrdersWithFulfillerStatusReceivedOrNull() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setFulfillerStatus(FulfillerStatus.valueOf("RECEIVED")).setIncludeNullFulfillerStatus(new Boolean(true)).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(12, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertTrue(order.getFulfillerStatus() == FulfillerStatus.RECEIVED || order.getFulfillerStatus() == null);
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldReturnOrdersWithFulfillerStatusNotNull() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setIncludeNullFulfillerStatus(new Boolean(false)).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(3, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertTrue(order.getFulfillerStatus() != null);
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldReturnOrdersWithFulfillerStatusNull() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setIncludeNullFulfillerStatus(new Boolean(true)).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(10, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertNull(order.getFulfillerStatus());
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldreturnDiscontinuedOrders() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setAction(Action.valueOf("DISCONTINUE")).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(2, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertEquals(Action.DISCONTINUE, order.getAction());
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldNotReturnDiscontinuedOrders() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setExcludeDiscontinueOrders(true).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(11, orders.size());
//        Iterator var3 = orders.iterator();
//
//        while(var3.hasNext()) {
//            Order order = (Order)var3.next();
//            Assertions.assertNotEquals(order.getAction(), Action.DISCONTINUE);
//        }
//
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByCareSetting() {
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setCareSetting(outPatient).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(12, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByConcepts() {
//        List<Concept> concepts = new ArrayList();
//        concepts.add(this.conceptService.getConcept(88));
//        concepts.add(this.conceptService.getConcept(3));
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setConcepts(concepts).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(6, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByOrderTypes() {
//        List<OrderType> orderTypes = new ArrayList();
//        orderTypes.add(this.orderService.getOrderType(1));
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setOrderTypes(orderTypes).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(10, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByActivatedOnOrBeforeDate() {
//        Date activatedOnOrBeforeDate = (new GregorianCalendar(2008, 7, 19)).getTime();
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setActivatedOnOrBeforeDate(activatedOnOrBeforeDate).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(11, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByActivatedOnOrAfterDate() {
//        Date activatedOnOrAfterDate = (new GregorianCalendar(2008, 7, 19, 12, 0)).getTime();
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setActivatedOnOrAfterDate(activatedOnOrAfterDate).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(3, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetOrdersByIncludeVoided() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setIncludeVoided(true).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(14, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersByCareSettingAndOrderType() {
//        CareSetting outPatient = this.orderService.getCareSetting(1);
//        List<OrderType> orderTypes = new ArrayList();
//        orderTypes.add(this.orderService.getOrderType(2));
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setCareSetting(outPatient).setOrderTypes(orderTypes).build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(3, orders.size());
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersByOrderNumber() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setOrderNumber("ORD-7").build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(1, orders.size());
//        Assertions.assertEquals("2c96f25c-4949-4f72-9931-d808fbc226df", ((Order)orders.iterator().next()).getUuid());
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersByOrderNumberEvenIfCaseDoesNotMatch() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setOrderNumber("ord-7").build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(1, orders.size());
//        Assertions.assertEquals("2c96f25c-4949-4f72-9931-d808fbc226df", ((Order)orders.iterator().next()).getUuid());
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersByAccessionNumber() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setAccessionNumber("ACC-123").build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(1, orders.size());
//        Assertions.assertEquals("e1f95924-697a-11e3-bd76-0800271c1b75", ((Order)orders.iterator().next()).getUuid());
//    }
//
//    @Test
//    public void getOrders_shouldGetTheOrdersByAccessionNumberEvenIfCaseDoesNotMatch() {
//        OrderSearchCriteria orderSearchCriteria = (new OrderSearchCriteriaBuilder()).setAccessionNumber("acc-123").build();
//        List<Order> orders = this.orderService.getOrders(orderSearchCriteria);
//        Assertions.assertEquals(1, orders.size());
//        Assertions.assertEquals("e1f95924-697a-11e3-bd76-0800271c1b75", ((Order)orders.iterator().next()).getUuid());
//    }
//
//    @Test
//    public void getAllOrdersByPatient_shouldFailIfPatientIsNull() {
//        IllegalArgumentException exception = (IllegalArgumentException)Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            this.orderService.getAllOrdersByPatient((Patient)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Patient is required"));
//    }
//
//    @Test
//    public void getAllOrdersByPatient_shouldGetAllTheOrdersForTheSpecifiedPatient() {
//        Assertions.assertEquals(12, this.orderService.getAllOrdersByPatient(this.patientService.getPatient(2)).size());
//        Assertions.assertEquals(2, this.orderService.getAllOrdersByPatient(this.patientService.getPatient(7)).size());
//    }
//
//    @Test
//    public void saveOrder_shouldSetOrderTypeIfNullButMappedToTheConceptClass() {
//        TestOrder order = new TestOrder();
//        order.setPatient(this.patientService.getPatient(7));
//        order.setConcept(this.conceptService.getConcept(5497));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertEquals(2, order.getOrderType().getOrderTypeId());
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfOrderTypeIsNullAndNotMappedToTheConceptClass() {
//        Order order = new Order();
//        order.setPatient(this.patientService.getPatient(7));
//        order.setConcept(this.conceptService.getConcept(9));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        OrderEntryException exception = (OrderEntryException)Assertions.assertThrows(OrderEntryException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.type.cannot.determine"));
//    }
//
//    @Test
//    public void saveOrderType_shouldAddANewOrderTypeToTheDatabase() {
//        int orderTypeCount = this.orderService.getOrderTypes(true).size();
//        OrderType orderType = new OrderType();
//        orderType.setName("New Order");
//        orderType.setJavaClassName("org.openmrs.NewTestOrder");
//        orderType.setDescription("New order type for testing");
//        orderType.setRetired(false);
//        orderType = this.orderService.saveOrderType(orderType);
//        Assertions.assertNotNull(orderType);
//        Assertions.assertEquals("New Order", orderType.getName());
//        Assertions.assertNotNull(orderType.getId());
//        Assertions.assertEquals(orderTypeCount + 1, this.orderService.getOrderTypes(true).size());
//    }
//
//    @Test
//    public void saveOrderType_shouldEditAnExistingOrderType() {
//        OrderType orderType = this.orderService.getOrderType(1);
//        Assertions.assertNull(orderType.getDateChanged());
//        Assertions.assertNull(orderType.getChangedBy());
//        String newDescription = "new";
//        orderType.setDescription("new");
//        this.orderService.saveOrderType(orderType);
//        Context.flushSession();
//        Assertions.assertNotNull(orderType.getDateChanged());
//        Assertions.assertNotNull(orderType.getChangedBy());
//    }
//
//    @Test
//    public void purgeOrderType_shouldDeleteOrderTypeIfNotInUse() {
//        Integer id = 13;
//        OrderType orderType = this.orderService.getOrderType(id);
//        Assertions.assertNotNull(orderType);
//        this.orderService.purgeOrderType(orderType);
//        Assertions.assertNull(this.orderService.getOrderType(id));
//    }
//
//    @Test
//    public void purgeOrderType_shouldNotAllowDeletingAnOrderTypeThatIsInUse() {
//        OrderType orderType = this.orderService.getOrderType(1);
//        Assertions.assertNotNull(orderType);
//        CannotDeleteObjectInUseException exception = (CannotDeleteObjectInUseException)Assertions.assertThrows(CannotDeleteObjectInUseException.class, () -> {
//            this.orderService.purgeOrderType(orderType);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.type.cannot.delete")));
//    }
//
//    @Test
//    public void retireOrderType_shouldRetireOrderType() {
//        OrderType orderType = this.orderService.getOrderType(15);
//        Assertions.assertFalse(orderType.getRetired());
//        Assertions.assertNull(orderType.getRetiredBy());
//        Assertions.assertNull(orderType.getRetireReason());
//        Assertions.assertNull(orderType.getDateRetired());
//        this.orderService.retireOrderType(orderType, "Retire for testing purposes");
//        orderType = this.orderService.getOrderType(15);
//        Assertions.assertTrue(orderType.getRetired());
//        Assertions.assertNotNull(orderType.getRetiredBy());
//        Assertions.assertNotNull(orderType.getRetireReason());
//        Assertions.assertNotNull(orderType.getDateRetired());
//    }
//
//    @Test
//    public void unretireOrderType_shouldUnretireOrderType() {
//        OrderType orderType = this.orderService.getOrderType(16);
//        Assertions.assertTrue(orderType.getRetired());
//        Assertions.assertNotNull(orderType.getRetiredBy());
//        Assertions.assertNotNull(orderType.getRetireReason());
//        Assertions.assertNotNull(orderType.getDateRetired());
//        this.orderService.unretireOrderType(orderType);
//        orderType = this.orderService.getOrderType(16);
//        Assertions.assertFalse(orderType.getRetired());
//        Assertions.assertNull(orderType.getRetiredBy());
//        Assertions.assertNull(orderType.getRetireReason());
//        Assertions.assertNull(orderType.getDateRetired());
//    }
//
//    @Test
//    public void getOrderSubTypes_shouldGetAllSubOrderTypesWithRetiredOrderTypes() {
//        List<OrderType> orderTypeList = this.orderService.getSubtypes(this.orderService.getOrderType(2), true);
//        Assertions.assertEquals(7, orderTypeList.size());
//    }
//
//    @Test
//    public void getOrderSubTypes_shouldGetAllSubOrderTypesWithoutRetiredOrderTypes() {
//        List<OrderType> orderTypeList = this.orderService.getSubtypes(this.orderService.getOrderType(2), false);
//        Assertions.assertEquals(6, orderTypeList.size());
//    }
//
//    @Test
//    public void saveOrder_shouldDefaultToCareSettingAndOrderTypeDefinedInTheOrderContextIfNull() {
//        Order order = new TestOrder();
//        order.setPatient(this.patientService.getPatient(7));
//        Concept trimune30 = this.conceptService.getConcept(792);
//        order.setConcept(trimune30);
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(3));
//        order.setDateActivated(new Date());
//        OrderType expectedOrderType = this.orderService.getOrderType(2);
//        CareSetting expectedCareSetting = this.orderService.getCareSetting(1);
//        OrderContext orderContext = new OrderContext();
//        orderContext.setOrderType(expectedOrderType);
//        orderContext.setCareSetting(expectedCareSetting);
//        Order order2 = this.orderService.saveOrder(order, orderContext);
//        Assertions.assertFalse(expectedOrderType.getConceptClasses().contains(trimune30.getConceptClass()));
//        Assertions.assertEquals(expectedOrderType, order2.getOrderType());
//        Assertions.assertEquals(expectedCareSetting, order2.getCareSetting());
//    }
//
//    @Test
//    public void getDiscontinuationOrder_shouldReturnDiscontinuationOrderIfOrderHasBeenDiscontinued() {
//        Order order = this.orderService.getOrder(111);
//        Order discontinuationOrder = this.orderService.discontinueOrder(order, "no reason", new Date(), this.providerService.getProvider(1), order.getEncounter());
//        Order foundDiscontinuationOrder = this.orderService.getDiscontinuationOrder(order);
//        MatcherAssert.assertThat(foundDiscontinuationOrder, Matchers.is(discontinuationOrder));
//    }
//
//    @Test
//    public void getDiscontinuationOrder_shouldReturnNullIfOrderHasNotBeenDiscontinued() {
//        Order order = this.orderService.getOrder(111);
//        Order discontinuationOrder = this.orderService.getDiscontinuationOrder(order);
//        MatcherAssert.assertThat(discontinuationOrder, Matchers.is(Matchers.nullValue()));
//    }
//
//    @Test
//    public void getOrderTypeByConceptClass_shouldGetOrderTypeMappedToTheGivenConceptClass() {
//        OrderType orderType = this.orderService.getOrderTypeByConceptClass(Context.getConceptService().getConceptClass(1));
//        Assertions.assertNotNull(orderType);
//        Assertions.assertEquals(2, orderType.getOrderTypeId());
//    }
//
//    @Test
//    public void getOrderTypeByConcept_shouldGetOrderTypeMappedToTheGivenConcept() {
//        OrderType orderType = this.orderService.getOrderTypeByConcept(Context.getConceptService().getConcept(5089));
//        Assertions.assertNotNull(orderType);
//        Assertions.assertEquals(2, orderType.getOrderTypeId());
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfConceptInPreviousOrderDoesNotMatchThatOfTheRevisedOrder() {
//        Order previousOrder = this.orderService.getOrder(7);
//        Order order = previousOrder.cloneForRevision();
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        Concept newConcept = this.conceptService.getConcept(5089);
//        Assertions.assertFalse(previousOrder.getConcept().equals(newConcept));
//        order.setConcept(newConcept);
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfTheExistingDrugOrderMatchesTheConceptAndNotDrugOfTheRevisedOrder() {
//        DrugOrder orderToDiscontinue = (DrugOrder)this.orderService.getOrder(5);
//        Drug discontinuationOrderDrug = new Drug();
//        discontinuationOrderDrug.setConcept(orderToDiscontinue.getConcept());
//        discontinuationOrderDrug = this.conceptService.saveDrug(discontinuationOrderDrug);
//        Assertions.assertNotEquals(discontinuationOrderDrug, orderToDiscontinue.getDrug());
//        Assertions.assertNotNull(orderToDiscontinue.getDrug());
//        DrugOrder order = orderToDiscontinue.cloneForRevision();
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setDrug(discontinuationOrderDrug);
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfTheOrderTypeOfThePreviousOrderDoesNotMatch() {
//        Order order = this.orderService.getOrder(7);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Order discontinuationOrder = order.cloneForDiscontinuing();
//        OrderType orderType = this.orderService.getOrderType(7);
//        Assertions.assertNotEquals(discontinuationOrder.getOrderType(), orderType);
//        Assertions.assertTrue(OrderUtil.isType(discontinuationOrder.getOrderType(), orderType));
//        discontinuationOrder.setOrderType(orderType);
//        discontinuationOrder.setOrderer(Context.getProviderService().getProvider(1));
//        discontinuationOrder.setEncounter(Context.getEncounterService().getEncounter(6));
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(discontinuationOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.type.doesnot.match")));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfTheJavaTypeOfThePreviousOrderDoesNotMatch() throws Exception {
//        HibernateSessionFactoryBean sessionFactoryBean = (HibernateSessionFactoryBean)this.applicationContext.getBean("&sessionFactory");
//        Configuration configuration = sessionFactoryBean.getConfiguration();
//        HibernateAdministrationDAO adminDAO = (HibernateAdministrationDAO)this.applicationContext.getBean("adminDAO");
//        StandardServiceRegistry standardRegistry = (new StandardServiceRegistryBuilder()).configure().applySettings(configuration.getProperties()).build();
//        Metadata metaData = (new MetadataSources(standardRegistry)).addAnnotatedClass(Allergy.class).addAnnotatedClass(Encounter.class).addAnnotatedClass(OrderServiceTest.SomeTestOrder.class).addAnnotatedClass(Diagnosis.class).addAnnotatedClass(Condition.class).addAnnotatedClass(Visit.class).getMetadataBuilder().build();
//        Field field = adminDAO.getClass().getDeclaredField("metadata");
//        field.setAccessible(true);
//        field.set(adminDAO, metaData);
//        Order order = this.orderService.getOrder(7);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Order discontinuationOrder = new OrderServiceTest.SomeTestOrder();
//        discontinuationOrder.setCareSetting(order.getCareSetting());
//        discontinuationOrder.setConcept(order.getConcept());
//        discontinuationOrder.setAction(Action.DISCONTINUE);
//        discontinuationOrder.setPreviousOrder(order);
//        discontinuationOrder.setPatient(order.getPatient());
//        Assertions.assertTrue(order.getOrderType().getJavaClass().isAssignableFrom(discontinuationOrder.getClass()));
//        discontinuationOrder.setOrderType(order.getOrderType());
//        discontinuationOrder.setOrderer(Context.getProviderService().getProvider(1));
//        discontinuationOrder.setEncounter(Context.getEncounterService().getEncounter(6));
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(discontinuationOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.class.doesnot.match")));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfTheCareSettingOfThePreviousOrderDoesNotMatch() {
//        Order order = this.orderService.getOrder(7);
//        Assertions.assertTrue(OrderUtilTest.isActiveOrder(order, (Date)null));
//        Order discontinuationOrder = order.cloneForDiscontinuing();
//        CareSetting careSetting = this.orderService.getCareSetting(2);
//        Assertions.assertNotEquals(discontinuationOrder.getCareSetting(), careSetting);
//        discontinuationOrder.setCareSetting(careSetting);
//        discontinuationOrder.setOrderer(Context.getProviderService().getProvider(1));
//        discontinuationOrder.setEncounter(Context.getEncounterService().getEncounter(6));
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(discontinuationOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.care.setting.doesnot.match")));
//    }
//
//    @Test
//    public void saveOrder_shouldSetConceptForDrugOrdersIfNull() {
//        Patient patient = this.patientService.getPatient(7);
//        CareSetting careSetting = this.orderService.getCareSetting(2);
//        OrderType orderType = this.orderService.getOrderTypeByName("Drug order");
//        DrugOrder order = new DrugOrder();
//        Encounter encounter = this.encounterService.getEncounter(3);
//        order.setEncounter(encounter);
//        order.setPatient(patient);
//        order.setDrug(this.conceptService.getDrug(2));
//        order.setCareSetting(careSetting);
//        order.setOrderer(Context.getProviderService().getProvider(1));
//        order.setDateActivated(encounter.getEncounterDatetime());
//        order.setOrderType(orderType);
//        order.setDosingType(FreeTextDosingInstructions.class);
//        order.setInstructions("None");
//        order.setDosingInstructions("Test Instruction");
//        this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertNotNull(order.getOrderId());
//    }
//
//    @Test
//    public void getDrugRoutes_shouldGetDrugRoutesAssociatedConceptPrividedInGlobalProperties() {
//        List<Concept> drugRoutesList = this.orderService.getDrugRoutes();
//        Assertions.assertEquals(1, drugRoutesList.size());
//        Assertions.assertEquals(22, ((Concept)drugRoutesList.get(0)).getConceptId());
//    }
//
//    @Test
//    public void voidOrder_shouldVoidAnOrder() {
//        Order order = this.orderService.getOrder(1);
//        Assertions.assertFalse(order.getVoided());
//        Assertions.assertNull(order.getDateVoided());
//        Assertions.assertNull(order.getVoidedBy());
//        Assertions.assertNull(order.getVoidReason());
//        this.orderService.voidOrder(order, "None");
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNotNull(order.getDateVoided());
//        Assertions.assertNotNull(order.getVoidedBy());
//        Assertions.assertNotNull(order.getVoidReason());
//    }
//
//    @Test
//    public void voidOrder_shouldUnsetDateStoppedOfThePreviousOrderIfTheSpecifiedOrderIsADiscontinuation() {
//        Order order = this.orderService.getOrder(22);
//        Assertions.assertEquals(Action.DISCONTINUE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Context.flushSession();
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//    }
//
//    @Test
//    public void voidOrder_shouldUnsetDateStoppedOfThePreviousOrderIfTheSpecifiedOrderIsARevision() {
//        Order order = this.orderService.getOrder(111);
//        Assertions.assertEquals(Action.REVISE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Context.flushSession();
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//    }
//
//    @Test
//    public void unvoidOrder_shouldUnvoidAnOrder() {
//        Order order = this.orderService.getOrder(8);
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNotNull(order.getDateVoided());
//        Assertions.assertNotNull(order.getVoidedBy());
//        Assertions.assertNotNull(order.getVoidReason());
//        this.orderService.unvoidOrder(order);
//        Assertions.assertFalse(order.getVoided());
//        Assertions.assertNull(order.getDateVoided());
//        Assertions.assertNull(order.getVoidedBy());
//        Assertions.assertNull(order.getVoidReason());
//    }
//
//    @Test
//    public void unvoidOrder_shouldStopThePreviousOrderIfTheSpecifiedOrderIsADiscontinuation() {
//        Order order = this.orderService.getOrder(22);
//        Assertions.assertEquals(Action.DISCONTINUE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Context.flushSession();
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//        this.orderService.unvoidOrder(order);
//        Context.flushSession();
//        Assertions.assertFalse(order.getVoided());
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//    }
//
//    @Test
//    public void unvoidOrder_shouldStopThePreviousOrderIfTheSpecifiedOrderIsARevision() {
//        Order order = this.orderService.getOrder(111);
//        Assertions.assertEquals(Action.REVISE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Context.flushSession();
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//        this.orderService.unvoidOrder(order);
//        Context.flushSession();
//        Assertions.assertFalse(order.getVoided());
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//    }
//
//    @Test
//    public void unvoidOrder_shouldFailForADiscontinuationOrderIfThePreviousOrderIsInactive() throws InterruptedException {
//        Order order = this.orderService.getOrder(22);
//        Assertions.assertEquals(Action.DISCONTINUE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//        this.orderService.discontinueOrder(previousOrder, "Testing", (Date)null, previousOrder.getOrderer(), previousOrder.getEncounter());
//        Thread.sleep(10L);
//        CannotUnvoidOrderException exception = (CannotUnvoidOrderException)Assertions.assertThrows(CannotUnvoidOrderException.class, () -> {
//            this.orderService.unvoidOrder(order);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.action.cannot.unvoid", new Object[]{"discontinuation"}, (Locale)null)));
//    }
//
//    @Test
//    public void unvoidOrder_shouldFailForAReviseOrderIfThePreviousOrderIsInactive() throws InterruptedException {
//        Order order = this.orderService.getOrder(111);
//        Assertions.assertEquals(Action.REVISE, order.getAction());
//        Order previousOrder = order.getPreviousOrder();
//        Assertions.assertNotNull(previousOrder.getDateStopped());
//        Assertions.assertFalse(order.getVoided());
//        this.orderService.voidOrder(order, "None");
//        Assertions.assertTrue(order.getVoided());
//        Assertions.assertNull(previousOrder.getDateStopped());
//        Order revise = previousOrder.cloneForRevision();
//        revise.setOrderer(order.getOrderer());
//        revise.setEncounter(order.getEncounter());
//        this.orderService.saveOrder(revise, (OrderContext)null);
//        Thread.sleep(10L);
//        CannotUnvoidOrderException exception = (CannotUnvoidOrderException)Assertions.assertThrows(CannotUnvoidOrderException.class, () -> {
//            this.orderService.unvoidOrder(order);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.action.cannot.unvoid", new Object[]{"revision"}, (Locale)null)));
//    }
//
//    @Test
//    public void getRevisionOrder_shouldReturnRevisionOrderIfOrderHasBeenRevised() {
//        Assertions.assertEquals(this.orderService.getOrder(111), this.orderService.getRevisionOrder(this.orderService.getOrder(1)));
//    }
//
//    @Test
//    public void getRevisionOrder_shouldReturnNullIfOrderHasNotBeenRevised() {
//        Assertions.assertNull(this.orderService.getRevisionOrder(this.orderService.getOrder(444)));
//    }
//
//    @Test
//    public void getDiscontinuationOrder_shouldReturnNullIfDcOrderIsVoided() {
//        Order order = this.orderService.getOrder(7);
//        Order discontinueOrder = this.orderService.discontinueOrder(order, "Some reason", new Date(), this.providerService.getProvider(1), this.encounterService.getEncounter(3));
//        this.orderService.voidOrder(discontinueOrder, "Invalid reason");
//        Order discontinuationOrder = this.orderService.getDiscontinuationOrder(order);
//        MatcherAssert.assertThat(discontinuationOrder, Matchers.is(Matchers.nullValue()));
//    }
//
//    @Test
//    public void getDrugDispensingUnits_shouldReturnTheUnionOfTheDosingAndDispensingUnits() {
//        List<Concept> dispensingUnits = this.orderService.getDrugDispensingUnits();
//        Assertions.assertEquals(2, dispensingUnits.size());
//        MatcherAssert.assertThat(dispensingUnits, Matchers.containsInAnyOrder(new Matcher[]{OpenmrsMatchers.hasId(50), OpenmrsMatchers.hasId(51)}));
//    }
//
//    @Test
//    public void getDrugDispensingUnits_shouldReturnAnEmptyListIfNothingIsConfigured() {
//        this.adminService.saveGlobalProperty(new GlobalProperty("order.drugDispensingUnitsConceptUuid", ""));
//        this.adminService.saveGlobalProperty(new GlobalProperty("order.drugDosingUnitsConceptUuid", ""));
//        MatcherAssert.assertThat(this.orderService.getDrugDispensingUnits(), Matchers.is(Matchers.empty()));
//    }
//
//    @Test
//    public void getDrugDosingUnits_shouldReturnAListIfGPIsSet() {
//        List<Concept> dosingUnits = this.orderService.getDrugDosingUnits();
//        Assertions.assertEquals(2, dosingUnits.size());
//        MatcherAssert.assertThat(dosingUnits, Matchers.containsInAnyOrder(new Matcher[]{OpenmrsMatchers.hasId(50), OpenmrsMatchers.hasId(51)}));
//    }
//
//    @Test
//    public void getDrugDosingUnits_shouldReturnAnEmptyListIfNothingIsConfigured() {
//        this.adminService.saveGlobalProperty(new GlobalProperty("order.drugDosingUnitsConceptUuid", ""));
//        MatcherAssert.assertThat(this.orderService.getDrugDosingUnits(), Matchers.is(Matchers.empty()));
//    }
//
//    @Test
//    public void getDurationUnits_shouldReturnAListIfGPIsSet() {
//        List<Concept> durationConcepts = this.orderService.getDurationUnits();
//        Assertions.assertEquals(1, durationConcepts.size());
//        Assertions.assertEquals(28, ((Concept)durationConcepts.get(0)).getConceptId());
//    }
//
//    @Test
//    public void getDurationUnits_shouldReturnAnEmptyListIfNothingIsConfigured() {
//        this.adminService.saveGlobalProperty(new GlobalProperty("order.durationUnitsConceptUuid", ""));
//        MatcherAssert.assertThat(this.orderService.getDurationUnits(), Matchers.is(Matchers.empty()));
//    }
//
//    @Test
//    public void getRevisionOrder_shouldNotReturnAVoidedRevisionOrder() {
//        Order order = this.orderService.getOrder(7);
//        Order revision1 = order.cloneForRevision();
//        revision1.setEncounter(order.getEncounter());
//        revision1.setOrderer(order.getOrderer());
//        this.orderService.saveOrder(revision1, (OrderContext)null);
//        Assertions.assertEquals(revision1, this.orderService.getRevisionOrder(order));
//        this.orderService.voidOrder(revision1, "Testing");
//        MatcherAssert.assertThat(this.orderService.getRevisionOrder(order), Matchers.is(Matchers.nullValue()));
//        Order revision2 = order.cloneForRevision();
//        revision2.setEncounter(order.getEncounter());
//        revision2.setOrderer(order.getOrderer());
//        this.orderService.saveOrder(revision2, (OrderContext)null);
//        Assertions.assertEquals(revision2, this.orderService.getRevisionOrder(order));
//    }
//
//    @Test
//    public void saveOrder_shouldPassForADiscontinuationOrderWithNoPreviousOrder() {
//        TestOrder dcOrder = new TestOrder();
//        dcOrder.setAction(Action.DISCONTINUE);
//        dcOrder.setPatient(this.patientService.getPatient(2));
//        dcOrder.setCareSetting(this.orderService.getCareSetting(2));
//        dcOrder.setConcept(this.conceptService.getConcept(5089));
//        dcOrder.setEncounter(this.encounterService.getEncounter(6));
//        dcOrder.setOrderer(this.providerService.getProvider(1));
//        this.orderService.saveOrder(dcOrder, (OrderContext)null);
//    }
//
//    @Test
//    public void getTestSpecimenSources_shouldReturnAListIfGPIsSet() {
//        List<Concept> specimenSourceList = this.orderService.getTestSpecimenSources();
//        Assertions.assertEquals(1, specimenSourceList.size());
//        Assertions.assertEquals(22, ((Concept)specimenSourceList.get(0)).getConceptId());
//    }
//
//    @Test
//    public void getTestSpecimenSources_shouldReturnAnEmptyListIfNothingIsConfigured() {
//        this.adminService.saveGlobalProperty(new GlobalProperty("order.testSpecimenSourcesConceptUuid", ""));
//        MatcherAssert.assertThat(this.orderService.getTestSpecimenSources(), Matchers.is(Matchers.empty()));
//    }
//
//    @Test
//    public void retireOrderType_shouldNotRetireIndependentField() {
//        OrderType orderType = this.orderService.getOrderType(2);
//        ConceptClass conceptClass = this.conceptService.getConceptClass(1);
//        Assertions.assertFalse(conceptClass.getRetired());
//        orderType.addConceptClass(conceptClass);
//        this.orderService.retireOrderType(orderType, "test retire reason");
//        Assertions.assertFalse(conceptClass.getRetired());
//    }
//
//    @Test
//    public void saveOrder_shouldSetOrderTypeOfDrugOrderToDrugOrderIfNotSetAndConceptNotMapped() {
//        Drug drug = this.conceptService.getDrug(2);
//        Concept unmappedConcept = this.conceptService.getConcept(113);
//        Assertions.assertNull(this.orderService.getOrderTypeByConcept(unmappedConcept));
//        drug.setConcept(unmappedConcept);
//        DrugOrder drugOrder = new DrugOrder();
//        Encounter encounter = this.encounterService.getEncounter(3);
//        drugOrder.setEncounter(encounter);
//        drugOrder.setPatient(this.patientService.getPatient(7));
//        drugOrder.setCareSetting(this.orderService.getCareSetting(1));
//        drugOrder.setOrderer(Context.getProviderService().getProvider(1));
//        drugOrder.setDateActivated(encounter.getEncounterDatetime());
//        drugOrder.setDrug(drug);
//        drugOrder.setDosingType(SimpleDosingInstructions.class);
//        drugOrder.setDose(300.0D);
//        drugOrder.setDoseUnits(this.conceptService.getConcept(50));
//        drugOrder.setQuantity(20.0D);
//        drugOrder.setQuantityUnits(this.conceptService.getConcept(51));
//        drugOrder.setFrequency(this.orderService.getOrderFrequency(3));
//        drugOrder.setRoute(this.conceptService.getConcept(22));
//        drugOrder.setNumRefills(10);
//        drugOrder.setOrderType((OrderType)null);
//        this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        Assertions.assertNotNull(drugOrder.getOrderType());
//        Assertions.assertEquals(this.orderService.getOrderTypeByUuid("131168f4-15f5-102d-96e4-000c29c2a5d7"), drugOrder.getOrderType());
//    }
//
//    @Test
//    public void saveOrder_shouldSetOrderTypeOfTestOrderToTestOrderIfNotSetAndConceptNotMapped() {
//        TestOrder testOrder = new TestOrder();
//        testOrder.setPatient(this.patientService.getPatient(7));
//        Concept unmappedConcept = this.conceptService.getConcept(113);
//        Assertions.assertNull(this.orderService.getOrderTypeByConcept(unmappedConcept));
//        testOrder.setConcept(unmappedConcept);
//        testOrder.setOrderer(this.providerService.getProvider(1));
//        testOrder.setCareSetting(this.orderService.getCareSetting(1));
//        Encounter encounter = this.encounterService.getEncounter(3);
//        testOrder.setEncounter(encounter);
//        testOrder.setDateActivated(encounter.getEncounterDatetime());
//        testOrder.setClinicalHistory("Patient had a negative reaction to the test in the past");
//        testOrder.setFrequency(this.orderService.getOrderFrequency(3));
//        testOrder.setSpecimenSource(this.conceptService.getConcept(22));
//        testOrder.setNumberOfRepeats(3);
//        this.orderService.saveOrder(testOrder, (OrderContext)null);
//        Assertions.assertNotNull(testOrder.getOrderType());
//        Assertions.assertEquals(this.orderService.getOrderTypeByUuid("52a447d3-a64a-11e3-9aeb-50e549534c5e"), testOrder.getOrderType());
//    }
//
//    @Test
//    public void saveOrder_shouldSetAutoExpireDateOfDrugOrderIfAutoExpireDateIsNotSet() throws ParseException {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-drugOrderAutoExpireDate.xml");
//        Drug drug = this.conceptService.getDrug(3000);
//        DrugOrder drugOrder = new DrugOrder();
//        Encounter encounter = this.encounterService.getEncounter(3);
//        drugOrder.setEncounter(encounter);
//        drugOrder.setPatient(this.patientService.getPatient(7));
//        drugOrder.setCareSetting(this.orderService.getCareSetting(1));
//        drugOrder.setOrderer(Context.getProviderService().getProvider(1));
//        drugOrder.setDrug(drug);
//        drugOrder.setDosingType(SimpleDosingInstructions.class);
//        drugOrder.setDose(300.0D);
//        drugOrder.setDoseUnits(this.conceptService.getConcept(50));
//        drugOrder.setQuantity(20.0D);
//        drugOrder.setQuantityUnits(this.conceptService.getConcept(51));
//        drugOrder.setFrequency(this.orderService.getOrderFrequency(3));
//        drugOrder.setRoute(this.conceptService.getConcept(22));
//        drugOrder.setNumRefills(0);
//        drugOrder.setOrderType((OrderType)null);
//        drugOrder.setDateActivated(TestUtil.createDateTime("2014-08-03"));
//        drugOrder.setDuration(20);
//        drugOrder.setDurationUnits(this.conceptService.getConcept(1001));
//        Order savedOrder = this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        Order loadedOrder = this.orderService.getOrder(savedOrder.getId());
//        Assertions.assertEquals(TestUtil.createDateTime("2014-08-22 23:59:59"), loadedOrder.getAutoExpireDate());
//    }
//
//    @Test
//    public void saveOrder_shouldSetAutoExpireDateForReviseOrderWithSimpleDosingInstructions() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-drugOrderAutoExpireDate.xml");
//        DrugOrder originalOrder = (DrugOrder)this.orderService.getOrder(111);
//        Assertions.assertTrue(originalOrder.isActive());
//        DrugOrder revisedOrder = originalOrder.cloneForRevision();
//        revisedOrder.setOrderer(originalOrder.getOrderer());
//        revisedOrder.setEncounter(originalOrder.getEncounter());
//        revisedOrder.setNumRefills(0);
//        revisedOrder.setAutoExpireDate((Date)null);
//        revisedOrder.setDuration(10);
//        revisedOrder.setDurationUnits(this.conceptService.getConcept(1001));
//        this.orderService.saveOrder(revisedOrder, (OrderContext)null);
//        Assertions.assertNotNull(revisedOrder.getAutoExpireDate());
//    }
//
//    @Test
//    public void saveOrder_shouldThrowAmbiguousOrderExceptionIfDisconnectingMultipleActiveOrdersForTheGivenConcepts() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-discontinueAmbiguousOrderByConcept.xml");
//        DrugOrder order = new DrugOrder();
//        order.setAction(Action.DISCONTINUE);
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setConcept(this.conceptService.getConcept(88));
//        order.setEncounter(this.encounterService.getEncounter(7));
//        order.setPatient(this.patientService.getPatient(9));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//    }
//
//    @Test
//    public void saveOrder_shouldThrowAmbiguousOrderExceptionIfDisconnectingMultipleActiveDrugOrdersWithTheSameDrug() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-ambiguousDrugOrders.xml");
//        DrugOrder order = new DrugOrder();
//        order.setAction(Action.DISCONTINUE);
//        order.setOrderReasonNonCoded("Discontinue this");
//        order.setDrug(this.conceptService.getDrug(3));
//        order.setEncounter(this.encounterService.getEncounter(7));
//        order.setPatient(this.patientService.getPatient(9));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(this.orderService.getCareSetting(1));
//        Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnKnownDrugOrderForTheSameDrugFormulationSpecified() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-drugOrdersWithSameConceptAndDifferentFormAndStrength.xml");
//        Patient patient = this.patientService.getPatient(2);
//        DrugOrder existingOrder = (DrugOrder)this.orderService.getOrder(1000);
//        Assertions.assertTrue(existingOrder.isActive());
//        DrugOrder order = new DrugOrder();
//        order.setPatient(patient);
//        order.setDrug(existingOrder.getDrug());
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(existingOrder.getCareSetting());
//        order.setDosingType(FreeTextDosingInstructions.class);
//        order.setDosingInstructions("2 for 5 days");
//        order.setQuantity(10.0D);
//        order.setQuantityUnits(this.conceptService.getConcept(51));
//        order.setNumRefills(2);
//        OrderContext orderContext = new OrderContext();
//        orderContext.setAttribute("PARALLEL_ORDERS", new String[]{existingOrder.getUuid()});
//        this.orderService.saveOrder(order, orderContext);
//        Assertions.assertNotNull(this.orderService.getOrder(order.getOrderId()));
//    }
//
//    @Test
//    public void getNonCodedDrugConcept_shouldReturnNullIfNothingIsConfigured() {
//        this.adminService.saveGlobalProperty(new GlobalProperty("drugOrder.drugOther", ""));
//        Assertions.assertNull(this.orderService.getNonCodedDrugConcept());
//    }
//
//    @Test
//    public void getNonCodedDrugConcept_shouldReturnAConceptIfGPIsSet() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        Concept nonCodedDrugConcept = this.orderService.getNonCodedDrugConcept();
//        Assertions.assertNotNull(nonCodedDrugConcept);
//        MatcherAssert.assertThat(nonCodedDrugConcept.getConceptId(), Matchers.is(5584));
//        Assertions.assertEquals(nonCodedDrugConcept.getName().getName(), "DRUG OTHER");
//    }
//
//    @Test
//    public void saveOrder_shouldPassIfAnActiveDrugOrderForTheSameConceptAndDifferentDrugNonCodedExists() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        Concept nonCodedConcept = this.orderService.getNonCodedDrugConcept();
//        DrugOrder duplicateOrder = (DrugOrder)this.orderService.getOrder(584);
//        Assertions.assertTrue(duplicateOrder.isActive());
//        Assertions.assertEquals(nonCodedConcept, duplicateOrder.getConcept());
//        DrugOrder drugOrder = duplicateOrder.copy();
//        drugOrder.setDrugNonCoded("non coded drug paracetemol");
//        Order savedOrder = this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        Assertions.assertNotNull(this.orderService.getOrder(savedOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfAnActiveDrugOrderForTheSameConceptAndDrugNonCodedAndCareSettingExists() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        Concept nonCodedConcept = this.orderService.getNonCodedDrugConcept();
//        DrugOrder duplicateOrder = (DrugOrder)this.orderService.getOrder(584);
//        Assertions.assertTrue(duplicateOrder.isActive());
//        Assertions.assertEquals(nonCodedConcept, duplicateOrder.getConcept());
//        DrugOrder drugOrder = duplicateOrder.copy();
//        drugOrder.setDrugNonCoded("non coded drug crocine");
//        AmbiguousOrderException exception = (AmbiguousOrderException)Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.have.more.than.one"));
//    }
//
//    @Test
//    public void saveOrder_shouldDiscontinuePreviousNonCodedOrderIfItIsNotAlreadyDiscontinued() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        DrugOrder previousOrder = (DrugOrder)this.orderService.getOrder(584);
//        DrugOrder drugOrder = previousOrder.cloneForDiscontinuing();
//        drugOrder.setPreviousOrder(previousOrder);
//        drugOrder.setDateActivated(new Date());
//        drugOrder.setOrderer(previousOrder.getOrderer());
//        drugOrder.setEncounter(previousOrder.getEncounter());
//        Order saveOrder = this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        Assertions.assertNotNull(previousOrder.getDateStopped(), "previous order should be discontinued");
//        Assertions.assertNotNull(this.orderService.getOrder(saveOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveOrder_shouldFailDiscontinueNonCodedDrugOrderIfOrderableOfPreviousAndNewOrderDontMatch() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        DrugOrder previousOrder = (DrugOrder)this.orderService.getOrder(584);
//        DrugOrder drugOrder = previousOrder.cloneForDiscontinuing();
//        drugOrder.setDrugNonCoded("non coded drug citrigine");
//        drugOrder.setPreviousOrder(previousOrder);
//        drugOrder.setDateActivated(new Date());
//        drugOrder.setOrderer(this.providerService.getProvider(1));
//        drugOrder.setEncounter(this.encounterService.getEncounter(6));
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(drugOrder, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void saveOrder_shouldFailIfDrugNonCodedInPreviousDrugOrderDoesNotMatchThatOfTheRevisedDrugOrder() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        DrugOrder previousOrder = (DrugOrder)this.orderService.getOrder(584);
//        DrugOrder order = previousOrder.cloneForRevision();
//        String drugNonCodedParacetemol = "non coded aspirin";
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        Assertions.assertFalse(previousOrder.getDrugNonCoded().equals(drugNonCodedParacetemol));
//        order.setDrugNonCoded(drugNonCodedParacetemol);
//        order.setPreviousOrder(previousOrder);
//        EditedOrderDoesNotMatchPreviousException exception = (EditedOrderDoesNotMatchPreviousException)Assertions.assertThrows(EditedOrderDoesNotMatchPreviousException.class, () -> {
//            this.orderService.saveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("The orderable of the previous order and the new one order don't match"));
//    }
//
//    @Test
//    public void saveOrder_shouldRevisePreviousNonCodedOrderIfItIsAlreadyExisting() {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-nonCodedDrugs.xml");
//        DrugOrder previousOrder = (DrugOrder)this.orderService.getOrder(584);
//        DrugOrder order = previousOrder.cloneForRevision();
//        order.setDateActivated(new Date());
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(6));
//        order.setAsNeeded(true);
//        order.setPreviousOrder(previousOrder);
//        DrugOrder saveOrder = (DrugOrder)this.orderService.saveOrder(order, (OrderContext)null);
//        Assertions.assertTrue(saveOrder.getAsNeeded());
//        Assertions.assertNotNull(this.orderService.getOrder(saveOrder.getOrderId()));
//    }
//
//    @Test
//    public void saveRetrospectiveOrder_shouldDiscontinueOrderInRetrospectiveEntry() throws ParseException {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-ordersWithAutoExpireDate.xml");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
//        Date originalOrderDateActivated = dateFormat.parse("2008-11-19 09:24:10.0");
//        Date discontinuationOrderDate = DateUtils.addDays(originalOrderDateActivated, 2);
//        Order originalOrder = this.orderService.getOrder(201);
//        Assertions.assertNull(originalOrder.getDateStopped());
//        Assertions.assertEquals(dateFormat.parse("2008-11-23 09:24:09.0"), originalOrder.getAutoExpireDate());
//        Assertions.assertFalse(originalOrder.isActive());
//        Assertions.assertTrue(originalOrder.isActive(discontinuationOrderDate));
//        Order discontinueationOrder = originalOrder.cloneForDiscontinuing();
//        discontinueationOrder.setPreviousOrder(originalOrder);
//        discontinueationOrder.setEncounter(this.encounterService.getEncounter(17));
//        discontinueationOrder.setOrderer(this.providerService.getProvider(1));
//        discontinueationOrder.setDateActivated(discontinuationOrderDate);
//        this.orderService.saveRetrospectiveOrder(discontinueationOrder, (OrderContext)null);
//        Assertions.assertNotNull(originalOrder.getDateStopped());
//        Assertions.assertEquals(discontinueationOrder.getAutoExpireDate(), discontinueationOrder.getDateActivated());
//    }
//
//    @Test
//    public void saveRetrospectiveOrder_shouldDiscontinueAndStopActiveOrderInRetrospectiveEntry() throws ParseException {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-ordersWithAutoExpireDate.xml");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
//        Date originalOrderDateActivated = dateFormat.parse("2008-11-19 09:24:10.0");
//        Date discontinuationOrderDate = DateUtils.addDays(originalOrderDateActivated, 2);
//        Order originalOrder = this.orderService.getOrder(202);
//        Assertions.assertNull(originalOrder.getDateStopped());
//        Assertions.assertEquals(dateFormat.parse("2008-11-23 09:24:09.0"), originalOrder.getAutoExpireDate());
//        Assertions.assertFalse(originalOrder.isActive());
//        Assertions.assertTrue(originalOrder.isActive(discontinuationOrderDate));
//        Order discontinuationOrder = originalOrder.cloneForDiscontinuing();
//        discontinuationOrder.setPreviousOrder((Order)null);
//        discontinuationOrder.setEncounter(this.encounterService.getEncounter(17));
//        discontinuationOrder.setOrderer(this.providerService.getProvider(1));
//        discontinuationOrder.setDateActivated(discontinuationOrderDate);
//        this.orderService.saveRetrospectiveOrder(discontinuationOrder, (OrderContext)null);
//        Assertions.assertNotNull(originalOrder.getDateStopped());
//        Assertions.assertEquals(discontinuationOrder.getAutoExpireDate(), discontinuationOrder.getDateActivated());
//    }
//
//    @Test
//    public void saveOrder_shouldNotRevisePreviousIfAlreadyStopped() throws ParseException {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-ordersWithAutoExpireDate.xml");
//        Order previousOrder = this.orderService.getOrder(203);
//        Date dateActivated = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).parse("2008-10-19 13:00:00");
//        Order order = previousOrder.cloneForRevision();
//        order.setDateActivated(dateActivated);
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setEncounter(this.encounterService.getEncounter(18));
//        order.setPreviousOrder(previousOrder);
//        CannotStopInactiveOrderException exception = (CannotStopInactiveOrderException)Assertions.assertThrows(CannotStopInactiveOrderException.class, () -> {
//            this.orderService.saveRetrospectiveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is(this.messageSourceService.getMessage("Order.cannot.discontinue.inactive")));
//    }
//
//    @Test
//    public void saveRetrospectiveOrder_shouldFailIfAnActiveDrugOrderForTheSameConceptAndCareSettingExistsAtOrderDateActivated() throws ParseException {
//        this.executeDataSet("org/openmrs/api/include/OrderServiceTest-ordersWithAutoExpireDate.xml");
//        Date newOrderDateActivated = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).parse("2008-11-19 13:00:10");
//        Patient patient = this.patientService.getPatient(12);
//        Concept orderConcept = this.conceptService.getConcept(88);
//        DrugOrder duplicateOrder = (DrugOrder)this.orderService.getOrder(202);
//        Assertions.assertTrue(duplicateOrder.isActive(newOrderDateActivated));
//        Assertions.assertEquals(orderConcept, duplicateOrder.getConcept());
//        DrugOrder order = new DrugOrder();
//        order.setPatient(patient);
//        order.setConcept(orderConcept);
//        order.setEncounter(this.encounterService.getEncounter(17));
//        order.setOrderer(this.providerService.getProvider(1));
//        order.setCareSetting(duplicateOrder.getCareSetting());
//        order.setDateActivated(newOrderDateActivated);
//        order.setDrug(duplicateOrder.getDrug());
//        order.setDose(duplicateOrder.getDose());
//        order.setDoseUnits(duplicateOrder.getDoseUnits());
//        order.setRoute(duplicateOrder.getRoute());
//        order.setFrequency(duplicateOrder.getFrequency());
//        order.setQuantity(duplicateOrder.getQuantity());
//        order.setQuantityUnits(duplicateOrder.getQuantityUnits());
//        order.setNumRefills(duplicateOrder.getNumRefills());
//        AmbiguousOrderException exception = (AmbiguousOrderException)Assertions.assertThrows(AmbiguousOrderException.class, () -> {
//            this.orderService.saveRetrospectiveOrder(order, (OrderContext)null);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Order.cannot.have.more.than.one"));
//    }
//
//    @Test
//    public void shouldSaveOrdersWithSortWeightWhenWithinAOrderGroup() {
//        this.executeDataSet("org/openmrs/api/include/OrderSetServiceTest-general.xml");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        OrderSet orderSet = Context.getOrderSetService().getOrderSet(2000);
//        OrderGroup orderGroup = new OrderGroup();
//        orderGroup.setOrderSet(orderSet);
//        orderGroup.setPatient(encounter.getPatient());
//        orderGroup.setEncounter(encounter);
//        Order firstOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Order secondOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1001).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Order orderWithoutOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).build();
//        Set<Order> orders = new LinkedHashSet();
//        orders.add(firstOrderWithOrderGroup);
//        orders.add(secondOrderWithOrderGroup);
//        orders.add(orderWithoutOrderGroup);
//        encounter.setOrders(orders);
//        Iterator var8 = encounter.getOrderGroups().iterator();
//
//        while(var8.hasNext()) {
//            OrderGroup og = (OrderGroup)var8.next();
//            if (og.getId() == null) {
//                Context.getOrderService().saveOrderGroup(og);
//            }
//        }
//
//        var8 = encounter.getOrdersWithoutOrderGroups().iterator();
//
//        Order o;
//        while(var8.hasNext()) {
//            o = (Order)var8.next();
//            if (o.getId() == null) {
//                Context.getOrderService().saveOrder(o, (OrderContext)null);
//            }
//        }
//
//        Context.flushSession();
//        OrderGroup savedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        o = Context.getOrderService().getOrderByUuid(orderWithoutOrderGroup.getUuid());
//        Assertions.assertEquals(firstOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(0)).getUuid(), "The first order in  savedOrderGroup is the same which is sent first in the List");
//        Assertions.assertEquals(secondOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(1)).getUuid(), "The second order in  savedOrderGroup is the same which is sent second in the List");
//        Assertions.assertNull(o.getSortWeight(), "The order which doesn't belong to an orderGroup has no sortWeight");
//        MatcherAssert.assertThat("The first order has a lower sortWeight than the second", ((Order)savedOrderGroup.getOrders().get(0)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(-1));
//    }
//
//    @Test
//    public void shouldSetTheCorrectSortWeightWhenAddingAnOrderInOrderGroup() {
//        this.executeDataSet("org/openmrs/api/include/OrderSetServiceTest-general.xml");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        OrderSet orderSet = Context.getOrderSetService().getOrderSet(2000);
//        OrderGroup orderGroup = new OrderGroup();
//        orderGroup.setOrderSet(orderSet);
//        orderGroup.setPatient(encounter.getPatient());
//        orderGroup.setEncounter(encounter);
//        Order firstOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Order secondOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1001).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Set<Order> orders = new LinkedHashSet();
//        orders.add(firstOrderWithOrderGroup);
//        orders.add(secondOrderWithOrderGroup);
//        encounter.setOrders(orders);
//        Iterator var7 = encounter.getOrderGroups().iterator();
//
//        while(var7.hasNext()) {
//            OrderGroup og = (OrderGroup)var7.next();
//            if (og.getId() == null) {
//                Context.getOrderService().saveOrderGroup(og);
//            }
//        }
//
//        Context.flushSession();
//        OrderGroup savedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Assertions.assertEquals(firstOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(0)).getUuid(), "The first order in  savedOrderGroup is the same which is sent first in the List");
//        Assertions.assertEquals(secondOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(1)).getUuid(), "The second order in  savedOrderGroup is the same which is sent second in the List");
//        MatcherAssert.assertThat("The first order has a lower sortWeight than the second", ((Order)savedOrderGroup.getOrders().get(0)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(-1));
//        Order newOrderWithoutAnyPosition = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(savedOrderGroup).build();
//        savedOrderGroup.addOrder(newOrderWithoutAnyPosition);
//        Context.getOrderService().saveOrderGroup(savedOrderGroup);
//        Context.flushSession();
//        OrderGroup secondSavedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Assertions.assertEquals(firstOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(0)).getUuid(), "The first order in  savedOrderGroup is the same which is sent first in the List");
//        Assertions.assertEquals(secondOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(1)).getUuid(), "The second order in  savedOrderGroup is the same which is sent second in the List");
//        Assertions.assertEquals(((Order)secondSavedOrderGroup.getOrders().get(2)).getUuid(), newOrderWithoutAnyPosition.getUuid(), "The third order in  savedOrderGroup is the same which is sent third in the List");
//        MatcherAssert.assertThat("The third order has a higher sortWeight than the second", ((Order)savedOrderGroup.getOrders().get(2)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(1));
//    }
//
//    @Test
//    public void shouldSetTheCorrectSortWeightWhenAddingAnOrderAtAPosition() {
//        this.executeDataSet("org/openmrs/api/include/OrderSetServiceTest-general.xml");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        OrderSet orderSet = Context.getOrderSetService().getOrderSet(2000);
//        OrderGroup orderGroup = new OrderGroup();
//        orderGroup.setOrderSet(orderSet);
//        orderGroup.setPatient(encounter.getPatient());
//        orderGroup.setEncounter(encounter);
//        Order firstOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Order secondOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1001).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Set<Order> orders = new LinkedHashSet();
//        orders.add(firstOrderWithOrderGroup);
//        orders.add(secondOrderWithOrderGroup);
//        encounter.setOrders(orders);
//        Iterator var7 = encounter.getOrderGroups().iterator();
//
//        while(var7.hasNext()) {
//            OrderGroup og = (OrderGroup)var7.next();
//            if (og.getId() == null) {
//                Context.getOrderService().saveOrderGroup(og);
//            }
//        }
//
//        Context.flushSession();
//        OrderGroup savedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Assertions.assertEquals(firstOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(0)).getUuid(), "The first order in  savedOrderGroup is the same which is sent first in the List");
//        Assertions.assertEquals(secondOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(1)).getUuid(), "The second order in  savedOrderGroup is the same which is sent second in the List");
//        MatcherAssert.assertThat("The first order has a lower sortWeight than the second", ((Order)savedOrderGroup.getOrders().get(0)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(-1));
//        Order newOrderAtPosition1 = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(savedOrderGroup).build();
//        Order newOrderAtPosition2 = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(savedOrderGroup).build();
//        savedOrderGroup.addOrder(newOrderAtPosition1, 0);
//        savedOrderGroup.addOrder(newOrderAtPosition2, 1);
//        Context.getOrderService().saveOrderGroup(savedOrderGroup);
//        OrderGroup secondSavedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Assertions.assertEquals(4, savedOrderGroup.getOrders().size());
//        Assertions.assertEquals(newOrderAtPosition1.getUuid(), ((Order)secondSavedOrderGroup.getOrders().get(0)).getUuid(), "The first order in  savedOrderGroup is the same which is sent first in the List");
//        Assertions.assertEquals(newOrderAtPosition2.getUuid(), ((Order)secondSavedOrderGroup.getOrders().get(1)).getUuid(), "The second order in  savedOrderGroup is the same which is sent second in the List");
//        Assertions.assertEquals(firstOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(2)).getUuid(), "The third order in  savedOrderGroup is the same which is sent third in the List");
//        Assertions.assertEquals(secondOrderWithOrderGroup.getUuid(), ((Order)savedOrderGroup.getOrders().get(3)).getUuid(), "The fourth order in  savedOrderGroup is the same which is sent first in the List");
//        MatcherAssert.assertThat("The third order has a lower sortWeight than the fourth", ((Order)savedOrderGroup.getOrders().get(2)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(3)).getSortWeight()), Matchers.is(-1));
//        MatcherAssert.assertThat("The second order has a lower sortWeight than the third", ((Order)savedOrderGroup.getOrders().get(1)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(2)).getSortWeight()), Matchers.is(-1));
//        MatcherAssert.assertThat("The first order has a lower sortWeight than the second", ((Order)savedOrderGroup.getOrders().get(0)).getSortWeight().compareTo(((Order)savedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(-1));
//    }
//
//    @Test
//    public void shouldSetTheCorrectSortWeightWhenAddingAnOrderWithANegativePosition() {
//        this.executeDataSet("org/openmrs/api/include/OrderSetServiceTest-general.xml");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        OrderSet orderSet = Context.getOrderSetService().getOrderSet(2000);
//        OrderGroup orderGroup = new OrderGroup();
//        orderGroup.setOrderSet(orderSet);
//        orderGroup.setPatient(encounter.getPatient());
//        orderGroup.setEncounter(encounter);
//        Order firstOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Order secondOrderWithOrderGroup = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1001).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(orderGroup).build();
//        Set<Order> orders = new LinkedHashSet();
//        orders.add(firstOrderWithOrderGroup);
//        orders.add(secondOrderWithOrderGroup);
//        encounter.setOrders(orders);
//        Iterator var7 = encounter.getOrderGroups().iterator();
//
//        while(var7.hasNext()) {
//            OrderGroup og = (OrderGroup)var7.next();
//            if (og.getId() == null) {
//                Context.getOrderService().saveOrderGroup(og);
//            }
//        }
//
//        Context.flushSession();
//        OrderGroup savedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Order newOrderWithNegativePosition = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(savedOrderGroup).build();
//        savedOrderGroup.addOrder(newOrderWithNegativePosition, -1);
//        Context.getOrderService().saveOrderGroup(savedOrderGroup);
//        Context.flushSession();
//        OrderGroup secondSavedOrderGroup = Context.getOrderService().getOrderGroupByUuid(orderGroup.getUuid());
//        Assertions.assertEquals(3, secondSavedOrderGroup.getOrders().size());
//        Assertions.assertEquals(newOrderWithNegativePosition.getUuid(), ((Order)secondSavedOrderGroup.getOrders().get(2)).getUuid(), "The new order gets added at the last position");
//        MatcherAssert.assertThat("The new order has a higher sortWeight than the second", ((Order)secondSavedOrderGroup.getOrders().get(2)).getSortWeight().compareTo(((Order)secondSavedOrderGroup.getOrders().get(1)).getSortWeight()), Matchers.is(1));
//        Order newOrderWithInvalidPosition = (new OrderBuilder()).withAction(Action.NEW).withPatient(7).withConcept(1000).withCareSetting(1).withOrderer(1).withEncounter(3).withDateActivated(new Date()).withOrderType(17).withUrgency(Urgency.ON_SCHEDULED_DATE).withScheduledDate(new Date()).withOrderGroup(savedOrderGroup).build();
//        APIException exception = (APIException)Assertions.assertThrows(APIException.class, () -> {
//            secondSavedOrderGroup.addOrder(newOrderWithInvalidPosition, secondSavedOrderGroup.getOrders().size() + 1);
//        });
//        MatcherAssert.assertThat(exception.getMessage(), Matchers.is("Cannot add a member which is out of range of the list"));
//    }
//
//    @Test
//    public void saveOrderGroup_shouldFailValidationIfAnyOrdersFailValidation() {
//        this.executeDataSet("org/openmrs/api/include/OrderSetServiceTest-general.xml");
//        Encounter encounter = this.encounterService.getEncounter(3);
//        OrderContext context = new OrderContext();
//        DrugOrder drugOrder = (new DrugOrderBuilder()).withPatient(encounter.getPatient().getPatientId()).withEncounter(encounter.getEncounterId()).withCareSetting(1).withOrderer(1).withOrderType(1).withDrug(2).withUrgency(Urgency.ROUTINE).withDateActivated(new Date()).build();
//        Exception expectedValidationError = null;
//
//        try {
//            Context.getOrderService().saveOrder(drugOrder, context);
//        } catch (Exception var10) {
//            expectedValidationError = var10;
//        }
//
//        Assertions.assertNotNull(expectedValidationError);
//        Assertions.assertEquals(ValidationException.class, expectedValidationError.getClass());
//        Assertions.assertTrue(expectedValidationError.getMessage().contains("Dose is required"));
//        OrderSet orderSet = Context.getOrderSetService().getOrderSet(2000);
//        OrderGroup orderGroup = new OrderGroup();
//        orderGroup.setOrderSet(orderSet);
//        orderGroup.setPatient(encounter.getPatient());
//        orderGroup.setEncounter(encounter);
//        orderGroup.addOrder(drugOrder);
//        drugOrder.setOrderGroup(orderGroup);
//        Exception expectedGroupValidationError = null;
//
//        try {
//            Context.getOrderService().saveOrderGroup(orderGroup);
//        } catch (Exception var9) {
//            expectedGroupValidationError = var9;
//        }
//
//        Assertions.assertNotNull(expectedGroupValidationError, "Validation should cause order group to fail to save");
//        Assertions.assertEquals(expectedValidationError.getMessage(), expectedGroupValidationError.getMessage());
//    }
//
//    @Test
//    public void getOrderGroupAttributeTypes_shouldReturnAllOrderGroupAttributeTypes() {
//        List<OrderGroupAttributeType> orderGroupAttributeTypes = this.orderService.getAllOrderGroupAttributeTypes();
//        Assertions.assertEquals(4, orderGroupAttributeTypes.size());
//    }
//
//    @Test
//    public void getOrderGroupAttributeType_shouldReturnNullIfNoOrderGroupAttributeTypeHasTheGivenId() {
//        Assertions.assertNull(this.orderService.getOrderGroupAttributeType(10));
//    }
//
//    @Test
//    public void getOrderGroupAttributeType_shouldReturnOrderGroupAttributeType() {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeType(2);
//        MatcherAssert.assertThat(orderGroupAttributeType.getId(), Matchers.is(2));
//    }
//
//    @Test
//    public void getOrderGroupAttributeTypeByUuid_shouldReturnOrderGroupAttributeTypeByUuid() {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeTypeByUuid("9cf1bce0-d18e-11ea-87d0-0242ac130003");
//        Assertions.assertEquals("Bacteriology", orderGroupAttributeType.getName());
//    }
//
//    @Test
//    public void saveOrderGroupAttributeType_shouldSaveOrderGroupAttributeTypeGivenOrderGroupAttributeType() throws ParseException {
//        int initialGroupOrderAttributeTypeCount = this.orderService.getAllOrderGroupAttributeTypes().size();
//        OrderGroupAttributeType orderGroupAttributeType = new OrderGroupAttributeType();
//        orderGroupAttributeType.setName("Surgery");
//        orderGroupAttributeType.setDatatypeClassname(FreeTextDatatype.class.getName());
//        this.orderService.saveOrderGroupAttributeType(orderGroupAttributeType);
//        Assertions.assertNotNull(orderGroupAttributeType.getId());
//        Assertions.assertEquals(initialGroupOrderAttributeTypeCount + 1, this.orderService.getAllOrderGroupAttributeTypes().size());
//    }
//
//    @Test
//    public void saveOrderGroupAttributeType_shouldEditAnExistingOrderGroupAttributeType() {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeType(4);
//        Assertions.assertEquals("ECG", orderGroupAttributeType.getName());
//        orderGroupAttributeType.setName("Laparascopy");
//        this.orderService.saveOrderGroupAttributeType(orderGroupAttributeType);
//        Assertions.assertEquals("Laparascopy", orderGroupAttributeType.getName());
//    }
//
//    @Test
//    public void retireOrderGroupAttributeType_shouldRetireOrderGroupAttributeType() throws ParseException {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeType(2);
//        Assertions.assertFalse(orderGroupAttributeType.getRetired());
//        Assertions.assertNotNull(orderGroupAttributeType.getRetiredBy());
//        Assertions.assertNull(orderGroupAttributeType.getRetireReason());
//        Assertions.assertNull(orderGroupAttributeType.getDateRetired());
//        this.orderService.retireOrderGroupAttributeType(orderGroupAttributeType, "Test Retire");
//        orderGroupAttributeType = this.orderService.getOrderGroupAttributeType(2);
//        Assertions.assertTrue(orderGroupAttributeType.getRetired());
//        Assertions.assertNotNull(orderGroupAttributeType.getRetiredBy());
//        Assertions.assertEquals("Test Retire", orderGroupAttributeType.getRetireReason());
//        Assertions.assertNotNull(orderGroupAttributeType.getDateRetired());
//    }
//
//    @Test
//    public void unretireOrderGroupAttributeType_shouldUnretireOrderGroupAttributeType() {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeType(4);
//        Assertions.assertTrue(orderGroupAttributeType.getRetired());
//        Assertions.assertNotNull(orderGroupAttributeType.getRetiredBy());
//        Assertions.assertNotNull(orderGroupAttributeType.getDateRetired());
//        Assertions.assertNotNull(orderGroupAttributeType.getRetireReason());
//        this.orderService.unretireOrderGroupAttributeType(orderGroupAttributeType);
//        Assertions.assertFalse(orderGroupAttributeType.getRetired());
//        Assertions.assertNull(orderGroupAttributeType.getRetiredBy());
//        Assertions.assertNull(orderGroupAttributeType.getDateRetired());
//        Assertions.assertNull(orderGroupAttributeType.getRetireReason());
//    }
//
//    @Test
//    public void getOrderGroupAttributeTypeByName_shouldReturnOrderGroupAttributeTypeUsingName() {
//        OrderGroupAttributeType orderGroupAttributeType = this.orderService.getOrderGroupAttributeTypeByName("Bacteriology");
//        Assertions.assertEquals("9cf1bce0-d18e-11ea-87d0-0242ac130003", orderGroupAttributeType.getUuid());
//    }
//
//    @Test
//    public void purgeOrderGroupAttributeType_shouldPurgeOrderGroupAttributeType() {
//        int initialOrderGroupAttributeTypeCount = this.orderService.getAllOrderGroupAttributeTypes().size();
//        this.orderService.purgeOrderGroupAttributeType(this.orderService.getOrderGroupAttributeType(4));
//        Assertions.assertEquals(initialOrderGroupAttributeTypeCount - 1, this.orderService.getAllOrderGroupAttributeTypes().size());
//    }
//
//    @Test
//    public void getOrderGroupAttributeByUuid_shouldReturnNullIfNonExistingUuidIsProvided() {
//        Assertions.assertNull(this.orderService.getOrderGroupAttributeTypeByUuid("cbf580ee-d7fb-11ea-87d0-0242ac130003"));
//    }
//
//    @Test
//    public void getOrderGroupAttributeByUuid_shouldReturnOrderGroupAttributeGivenUuid() {
//        OrderGroupAttribute orderGroupAttribute = this.orderService.getOrderGroupAttributeByUuid("86bdcc12-d18d-11ea-87d0-0242ac130003");
//        orderGroupAttribute.getValueReference();
//        Assertions.assertEquals("Test 1", orderGroupAttribute.getValueReference());
//        Assertions.assertEquals(1, orderGroupAttribute.getId());
//    }
//
//    @Entity
//    public class SomeTestOrder extends TestOrder {
//        @Id
//        private Integer orderId;
//        private Patient patient;
//        private OrderType orderType;
//        private Concept concept;
//        private String instructions;
//        private Date dateActivated;
//        private Date autoExpireDate;
//        private Encounter encounter;
//        private Provider orderer;
//        private Date dateStopped;
//        private Concept orderReason;
//        private String accessionNumber;
//        private String orderReasonNonCoded;
//        private Urgency urgency;
//        private String orderNumber;
//        private String commentToFulfiller;
//        private CareSetting careSetting;
//        private Date scheduledDate;
//        private Double sortWeight;
//        private Order previousOrder;
//        private Action action;
//        private OrderGroup orderGroup;
//        private FulfillerStatus fulfillerStatus;
//        private String fulfillerComment;
//        private Double dose;
//        private Concept doseUnits;
//        private OrderFrequency frequency;
//        private Boolean asNeeded;
//        private Double quantity;
//        private Concept quantityUnits;
//        private Drug drug;
//        private String asNeededCondition;
//        private Class<? extends DosingInstructions> dosingType;
//        private Integer numRefills;
//        private String dosingInstructions;
//        private Integer duration;
//        private Concept durationUnits;
//        private Concept route;
//        private String brandName;
//        private Boolean dispenseAsWritten;
//        private String drugNonCoded;
//        private Integer encounterId;
//
//
//        public SomeTestOrder() {
//            this.urgency = Urgency.ROUTINE;
//            this.action = Action.NEW;
//            this.asNeeded = false;
//            this.dosingType = SimpleDosingInstructions.class;
//            this.dispenseAsWritten = Boolean.FALSE;
//            this.encounterId = 11;
//        }
//    }
//}
