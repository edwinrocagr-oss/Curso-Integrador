package pe.edu.utp.cafegest;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pe.edu.utp.cafegest.config.Database;
import pe.edu.utp.cafegest.dao.ProductoDao;
import pe.edu.utp.cafegest.model.Producto;
import pe.edu.utp.cafegest.service.AuthService;
import pe.edu.utp.cafegest.service.VentaService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/** Interfaz JavaFX de CaféGest: navegación sencilla, catálogo, inventario y ventas. */
public class MainApp extends Application {
    private final ProductoDao dao = new ProductoDao();
    private final VentaService ventas = new VentaService();
    private final AuthService auth = new AuthService();
    private Stage stage;
    private String usuario;
    private String rol;
    private StackPane content;

    private static final String BG = "#F6F5F0";
    private static final String GREEN = "#174D3A";
    private static final String GREEN_LIGHT = "#E5F0E9";
    private static final String TEXT = "#24332D";
    private static final String MUTED = "#718078";
    private static final String BORDER = "#E2E7E2";
    private static final String CSS = "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;";

    @Override public void start(Stage stage) {
        this.stage = stage;
        try { Database.init(); if (auth.sinUsuarios()) setup(); else login(); }
        catch (Exception e) { error(e); }
        stage.setTitle("CaféGest | Gestión de cafetería");
        stage.setMinWidth(1050); stage.setMinHeight(680);
        stage.setWidth(1240); stage.setHeight(800); stage.show();
    }

    private Label title(String text) { Label l = new Label(text); l.setStyle("-fx-font-size:25px;-fx-font-weight:700;-fx-text-fill:"+TEXT+";"); return l; }
    private Label subtitle(String text) { Label l = new Label(text); l.setStyle("-fx-text-fill:"+MUTED+";-fx-font-size:13px;"); return l; }
    private VBox page(String heading, String sub) {
        VBox v = new VBox(18); v.setPadding(new Insets(28)); v.setStyle(CSS+"-fx-background-color:"+BG+";");
        v.getChildren().addAll(title(heading), subtitle(sub)); return v;
    }
    private VBox card() { VBox b = new VBox(10); b.setPadding(new Insets(18)); b.setStyle("-fx-background-color:white;-fx-background-radius:14;-fx-border-color:"+BORDER+";-fx-border-radius:14;"); return b; }
    private void show(Parent parent) { stage.setScene(new Scene(parent)); }
    private Button primary(String text) { Button b = new Button(text); b.setStyle("-fx-background-color:"+GREEN+";-fx-text-fill:white;-fx-font-weight:700;-fx-background-radius:9;-fx-padding:10 16 10 16;-fx-cursor:hand;"); return b; }
    private Button secondary(String text) { Button b = new Button(text); b.setStyle("-fx-background-color:"+GREEN_LIGHT+";-fx-text-fill:"+GREEN+";-fx-font-weight:700;-fx-background-radius:9;-fx-padding:10 14 10 14;-fx-cursor:hand;"); return b; }
    private void setup() {
        VBox v = new VBox(15); v.setPadding(new Insets(38)); v.setAlignment(Pos.CENTER_LEFT); v.setMaxWidth(460);
        v.setStyle(CSS+"-fx-background-color:white;-fx-background-radius:18;");
        Label logo = new Label("☕ CaféGest"); logo.setStyle("-fx-font-size:27px;-fx-font-weight:bold;-fx-text-fill:"+GREEN+";");
        Label desc = subtitle("Configuración inicial · crea la cuenta administradora");
        PasswordField p = new PasswordField(); p.setPromptText("Contraseña (mínimo 10 caracteres)");
        PasswordField confirm = new PasswordField(); confirm.setPromptText("Repite la contraseña");
        Button b = primary("Crear administrador"); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> run(() -> { if (!p.getText().equals(confirm.getText())) throw new IllegalArgumentException("Las contraseñas no coinciden"); auth.crearAdminInicial(p.getText()); login(); }));
        v.getChildren().addAll(logo,desc,p,confirm,b); show(new StackPane(v));
    }
    private void login() {
        VBox v = new VBox(15); v.setPadding(new Insets(34)); v.setAlignment(Pos.CENTER_LEFT); v.setMaxWidth(420);
        v.setStyle(CSS+"-fx-background-color:white;-fx-background-radius:18;-fx-effect:dropshadow(gaussian, #00000018, 20, 0, 0, 4);");
        Label logo = new Label("☕ CaféGest"); logo.setStyle("-fx-font-size:30px;-fx-font-weight:800;-fx-text-fill:"+GREEN+";");
        Label s = subtitle("Tu cafetería, organizada en un solo lugar.");
        TextField u = new TextField(); u.setPromptText("Usuario"); PasswordField p = new PasswordField(); p.setPromptText("Contraseña");
        Button b = primary("Ingresar"); b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> run(() -> { String r = auth.login(u.getText().trim(),p.getText()); if (r==null) throw new IllegalArgumentException("Usuario o contraseña incorrectos"); usuario=u.getText().trim(); rol=r; home(); }));
        v.getChildren().addAll(logo,s,new Separator(),new Label("Iniciar sesión"),u,p,b);
        StackPane root = new StackPane(v); root.setPadding(new Insets(30)); root.setStyle("-fx-background-color:"+BG+";"); show(root);
    }
    private void home() {
        BorderPane root = new BorderPane(); root.setStyle(CSS+"-fx-background-color:"+BG+";");
        VBox nav = new VBox(8); nav.setPadding(new Insets(24,14,20,14)); nav.setPrefWidth(225); nav.setStyle("-fx-background-color:"+GREEN+";");
        Label logo = new Label("☕ CaféGest"); logo.setStyle("-fx-text-fill:white;-fx-font-size:23px;-fx-font-weight:800;");
        Label tagline = new Label("GESTIÓN DE CAFETERÍA"); tagline.setStyle("-fx-text-fill:#B8D0C1;-fx-font-size:10px;-fx-font-weight:bold;");
        nav.getChildren().addAll(logo,tagline,new Separator());
        content = new StackPane(); content.setPadding(new Insets(0)); root.setLeft(nav); root.setCenter(content);
        String[][] items = {{"⌂","Resumen"},{"☕","Productos"},{"▤","Inventario"},{"＋","Nueva venta"},{"◷","Historial y reportes"}};
        for (int i=0;i<items.length;i++) { final int index=i; Button b=new Button(items[i][0]+"    "+items[i][1]); b.setAlignment(Pos.CENTER_LEFT); b.setMaxWidth(Double.MAX_VALUE); b.setPrefHeight(44); b.setStyle(navStyle(false)); b.setOnMouseEntered(e->b.setStyle(navStyle(true))); b.setOnMouseExited(e->b.setStyle(navStyle(false))); b.setOnAction(e->navigate(index)); nav.getChildren().add(b); }
        Region spacer=new Region(); VBox.setVgrow(spacer,Priority.ALWAYS); Label user=new Label("●  "+usuario+"\n    "+rol); user.setStyle("-fx-text-fill:white;-fx-padding:12 5 12 5;"); Button logout=new Button("↪   Cerrar sesión"); logout.setMaxWidth(Double.MAX_VALUE); logout.setStyle(navStyle(false)); logout.setOnAction(e->login()); nav.getChildren().addAll(spacer,user,logout);
        navigate(0); show(root);
    }
    private String navStyle(boolean hover) { return "-fx-background-color:"+(hover?"#2C644F":"transparent")+";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-padding:12 10 12 10;-fx-background-radius:9;-fx-cursor:hand;"; }
    private void navigate(int index) { try { Node n=switch(index){case 0->dashboard();case 1->productos();case 2->inventario();case 3->venta();default->reportes();}; content.getChildren().setAll(n); } catch(Exception e){error(e);} }

    private Node dashboard() throws Exception {
        VBox v=page("Resumen de la cafetería","Hola, "+usuario+". Aquí tienes el estado de tu negocio.");
        List<Producto> ps=dao.listar(); long bajos=ps.stream().filter(p->p.stock()<=p.minimo()).count();
        int ventasHoy=0; BigDecimal ingresosHoy=BigDecimal.ZERO; int ventasTotal=0;
        try(Connection c=Database.open();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*),COALESCE(SUM(total),0) FROM ventas WHERE CAST(fecha AS DATE)=CURRENT_DATE")){r.next();ventasHoy=r.getInt(1);ingresosHoy=r.getBigDecimal(2);}
        try(Connection c=Database.open();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM ventas")){r.next();ventasTotal=r.getInt(1);}
        HBox metrics=new HBox(14); metrics.getChildren().addAll(metric("Ventas de hoy",""+ventasHoy,"▣","#E5F0E9"),metric("Ingresos de hoy","S/ "+ingresosHoy.toPlainString(),"S/","#F9EEDC"),metric("Productos activos",""+ps.size(),"☕","#E7ECF8"),metric("Stock por revisar",""+bajos,"!",bajos>0?"#F9E5E1":"#E5F0E9"));
        for(Node n:metrics.getChildren()) HBox.setHgrow(n,Priority.ALWAYS);
        VBox low=card(); low.getChildren().addAll(new Label("Inventario que necesita atención"),subtitle("Productos que llegaron al mínimo o están por debajo."));
        List<Producto> lowItems=ps.stream().filter(p->p.stock()<=p.minimo()).limit(5).toList();
        if(lowItems.isEmpty()) low.getChildren().add(new Label("✓ Todo bien: no hay productos con stock bajo."));
        else for(Producto p:lowItems) low.getChildren().add(stockLine(p));
        HBox actions=new HBox(10); Button sale=primary("＋  Registrar nueva venta"); sale.setOnAction(e->navigate(3)); Button inv=secondary("▤  Revisar inventario"); inv.setOnAction(e->navigate(2)); actions.getChildren().addAll(sale,inv);
        v.getChildren().addAll(metrics,low,actions,subtitle("Ventas registradas históricamente: "+ventasTotal)); return v;
    }
    private VBox metric(String name,String value,String icon,String tint){VBox b=card();b.setMinWidth(145);Label i=new Label(icon);i.setStyle("-fx-background-color:"+tint+";-fx-text-fill:"+GREEN+";-fx-padding:8;-fx-background-radius:9;-fx-font-size:16px;");Label val=new Label(value);val.setStyle("-fx-font-size:23px;-fx-font-weight:800;-fx-text-fill:"+TEXT+";");b.getChildren().addAll(i,subtitle(name),val);return b;}
    private Node stockLine(Producto p){HBox row=new HBox(10);row.setAlignment(Pos.CENTER_LEFT);Label n=new Label(p.nombre()+"  ·  "+p.categoria());Label qty=new Label(p.stock()+" unidades");qty.setStyle("-fx-font-weight:bold;-fx-text-fill:"+(p.stock()==0?"#B42318":"#9A5B08")+";");Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);row.getChildren().addAll(n,spacer,qty);return row;}

    private Node productos() throws Exception {
        VBox v=page("Productos","Administra el menú, precios y stock inicial de cada producto.");
        TableView<Producto> t=productTable(); TextField search=new TextField();search.setPromptText("Buscar por nombre o categoría...");
        List<Producto> all=new ArrayList<>(dao.listar()); t.getItems().setAll(all); search.textProperty().addListener((o,a,b)->t.getItems().setAll(all.stream().filter(p->(p.nombre()+" "+p.categoria()).toLowerCase().contains(b.toLowerCase())).toList()));
        TextField nombre=new TextField();nombre.setPromptText("Nombre del producto"); ComboBox<String> cat=new ComboBox<>(FXCollections.observableArrayList("Café caliente","Café frío","Bebidas especiales","Té e infusiones","Panadería","Postres","Sándwiches","Otros"));cat.setPromptText("Categoría");cat.setPrefWidth(180);
        TextField precio=new TextField();precio.setPromptText("Precio S/");TextField stock=new TextField();stock.setPromptText("Stock inicial");TextField minimo=new TextField("5");minimo.setPromptText("Mínimo");
        HBox fields=new HBox(8,nombre,cat,precio,stock,minimo);for(Node n:fields.getChildren())if(n instanceof TextField)HBox.setHgrow(n,Priority.ALWAYS);
        Button save=primary("Guardar producto");Button update=secondary("Actualizar seleccionado");Button deactivate=secondary("Desactivar");Button clear=secondary("Limpiar");
        Runnable refresh=()->{try{all.clear();all.addAll(dao.listar());t.getItems().setAll(all);search.clear();}catch(Exception ex){error(ex);}};
        save.setOnAction(e->run(()->{validateProduct(nombre,cat,precio,stock,minimo);dao.guardar(null,nombre.getText().trim(),cat.getValue(),new BigDecimal(precio.getText()),Integer.parseInt(stock.getText()),Integer.parseInt(minimo.getText()));refresh.run();clearFields(nombre,cat,precio,stock,minimo);}));
        update.setOnAction(e->run(()->{Producto p=selected(t);validateProduct(nombre,cat,precio,stock,minimo);dao.guardar(p.id(),nombre.getText().trim(),cat.getValue(),new BigDecimal(precio.getText()),Integer.parseInt(stock.getText()),Integer.parseInt(minimo.getText()));refresh.run();}));
        deactivate.setOnAction(e->run(()->{Producto p=selected(t);Alert a=new Alert(Alert.AlertType.CONFIRMATION,"¿Desactivar "+p.nombre()+"?",ButtonType.CANCEL,ButtonType.OK);a.setHeaderText("Confirmar acción");if(a.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK) {dao.desactivar(p.id());refresh.run();}}));
        clear.setOnAction(e->clearFields(nombre,cat,precio,stock,minimo));
        t.getSelectionModel().selectedItemProperty().addListener((o,a,p)->{if(p!=null){nombre.setText(p.nombre());cat.setValue(p.categoria());precio.setText(p.precio().toPlainString());stock.setText(""+p.stock());minimo.setText(""+p.minimo());}});
        if(!"ADMIN".equals(rol)){save.setDisable(true);update.setDisable(true);deactivate.setDisable(true);}
        VBox editor=card();editor.getChildren().addAll(new Label("Datos del producto"),fields,new HBox(8,save,update,deactivate,clear));
        v.getChildren().addAll(new HBox(search),t,editor);return v;
    }
    private void clearFields(TextField n,ComboBox<String> c,TextField p,TextField s,TextField m){n.clear();c.setValue(null);p.clear();s.clear();m.setText("5");}
    private void validateProduct(TextField n,ComboBox<String> c,TextField p,TextField s,TextField m){if(n.getText().isBlank()||c.getValue()==null)throw new IllegalArgumentException("Completa el nombre y la categoría.");BigDecimal price=new BigDecimal(p.getText().trim());int stockVal=Integer.parseInt(s.getText().trim()),min=Integer.parseInt(m.getText().trim());if(price.signum()<0||stockVal<0||min<0)throw new IllegalArgumentException("El precio y las cantidades no pueden ser negativos.");}
    private TableView<Producto> productTable() throws Exception {TableView<Producto> t=new TableView<>();String[] cols={"ID","Producto","Categoría","Precio","Existencias","Mínimo"};List<Function<Producto,String>> fs=List.of(p->""+p.id(),Producto::nombre,Producto::categoria,p->"S/ "+p.precio().toPlainString(),p->""+p.stock(),p->""+p.minimo());for(int i=0;i<cols.length;i++){final int j=i;TableColumn<Producto,String> c=new TableColumn<>(cols[i]);c.setCellValueFactory(d->new SimpleStringProperty(fs.get(j).apply(d.getValue())));c.setPrefWidth(new double[]{55,210,155,100,100,85}[i]);if(i==4)c.setCellFactory(col->new TableCell<>(){@Override protected void updateItem(String value,boolean empty){super.updateItem(value,empty);setText(empty?null:value);if(empty||getTableRow()==null||getTableRow().getItem()==null)setStyle("");else{Producto p=(Producto)getTableRow().getItem();setStyle(p.stock()<=p.minimo()?"-fx-text-fill:#B42318;-fx-font-weight:bold;":"-fx-text-fill:"+GREEN+";");}}});t.getColumns().add(c);}t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);t.setPrefHeight(300);return t;}
    private Producto selected(TableView<Producto> t){Producto p=t.getSelectionModel().getSelectedItem();if(p==null)throw new IllegalArgumentException("Selecciona primero un producto de la tabla.");return p;}

    private Node inventario() throws Exception {
        VBox v=page("Inventario","Controla existencias, identifica alertas y registra reposiciones.");List<Producto> ps=dao.listar();long low=ps.stream().filter(p->p.stock()<=p.minimo()).count();long zero=ps.stream().filter(p->p.stock()==0).count();
        HBox stats=new HBox(12,metric("Productos en alerta",""+low,"!","#F9EEDC"),metric("Agotados",""+zero,"×","#F9E5E1"),metric("Productos controlados",""+ps.size(),"▤",GREEN_LIGHT));for(Node n:stats.getChildren())HBox.setHgrow(n,Priority.ALWAYS);
        TableView<Producto> t=productTable();t.getItems().setAll(ps);TextField amount=new TextField();amount.setPromptText("Unidades a ingresar o retirar");TextField reason=new TextField();reason.setPromptText("Motivo (compra, merma, ajuste...)");HBox.setHgrow(reason,Priority.ALWAYS);
        Label hint=subtitle("Para reponer, escribe una cantidad positiva. Para retirar unidades, escribe una cantidad negativa.");Button add=primary("Registrar movimiento");
        add.setOnAction(e->run(()->{Producto p=selected(t);if(reason.getText().isBlank())throw new IllegalArgumentException("Indica el motivo del movimiento.");int delta=Integer.parseInt(amount.getText().trim());if(delta==0)throw new IllegalArgumentException("La cantidad no puede ser cero.");dao.ajustar(p.id(),delta,reason.getText().trim());t.getItems().setAll(dao.listar());amount.clear();reason.clear();new Alert(Alert.AlertType.INFORMATION,"Inventario actualizado correctamente.").showAndWait();}));if(!"ADMIN".equals(rol))add.setDisable(true);
        VBox movement=card();movement.getChildren().addAll(new Label("Registrar entrada o salida"),hint,new HBox(10,amount,reason,add));v.getChildren().addAll(stats,cardWithTable("Existencias actuales",t),movement);return v;
    }
    private VBox cardWithTable(String heading,Node child){VBox b=card();b.getChildren().addAll(new Label(heading),child);return b;}

    private record CartItem(Producto producto,int cantidad) { BigDecimal subtotal(){return producto.precio().multiply(BigDecimal.valueOf(cantidad));} }
    private Node venta() throws Exception {
        VBox v=page("Nueva venta","Busca un producto, agrégalo al carrito y confirma el cobro.");List<Producto> available=dao.listar();
        TextField search=new TextField();search.setPromptText("Buscar café, postre, bebida...");ComboBox<String> category=new ComboBox<>();category.getItems().add("Todas las categorías");available.stream().map(Producto::categoria).distinct().sorted().forEach(category.getItems()::add);category.setValue("Todas las categorías");
        ListView<Producto> catalog=new ListView<>();catalog.setPrefHeight(250);catalog.setCellFactory(lv->new ListCell<>(){@Override protected void updateItem(Producto p,boolean empty){super.updateItem(p,empty);if(empty||p==null){setText(null);setGraphic(null);}else{Label n=new Label(p.nombre());n.setStyle("-fx-font-weight:bold;-fx-text-fill:"+TEXT+";");Label d=new Label(p.categoria()+"  ·  S/ "+p.precio().toPlainString()+"  ·  Stock: "+p.stock());d.setStyle("-fx-text-fill:"+MUTED+";-fx-font-size:11px;");VBox box=new VBox(4,n,d);box.setPadding(new Insets(5));setGraphic(box);setText(null);}}});
        Runnable filter=()->catalog.getItems().setAll(available.stream().filter(p->p.stock()>0).filter(p->category.getValue()==null||category.getValue().equals("Todas las categorías")||p.categoria().equals(category.getValue())).filter(p->(p.nombre()+" "+p.categoria()).toLowerCase().contains(search.getText().toLowerCase())).toList());search.textProperty().addListener((o,a,b)->filter.run());category.valueProperty().addListener((o,a,b)->filter.run());filter.run();
        Spinner<Integer> qty=new Spinner<>(1,100,1);qty.setEditable(true);Button add=primary("Agregar al carrito  →");TableView<CartItem> cart=new TableView<>();cart.setPlaceholder(new Label("Tu carrito está vacío. Selecciona un producto para comenzar."));cart.setPrefHeight(250);
        TableColumn<CartItem,String> pn=new TableColumn<>("Producto");pn.setCellValueFactory(d->new SimpleStringProperty(d.getValue().producto().nombre()));TableColumn<CartItem,String> pq=new TableColumn<>("Cant.");pq.setCellValueFactory(d->new SimpleStringProperty(""+d.getValue().cantidad()));TableColumn<CartItem,String> pu=new TableColumn<>("Precio");pu.setCellValueFactory(d->new SimpleStringProperty("S/ "+d.getValue().producto().precio().toPlainString()));TableColumn<CartItem,String> pt=new TableColumn<>("Subtotal");pt.setCellValueFactory(d->new SimpleStringProperty("S/ "+d.getValue().subtotal().toPlainString()));cart.getColumns().addAll(pn,pq,pu,pt);cart.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Label total=new Label("Total: S/ 0.00");total.setStyle("-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:"+GREEN+";");
        add.setOnAction(e->{Producto p=catalog.getSelectionModel().getSelectedItem();if(p==null){new Alert(Alert.AlertType.WARNING,"Selecciona un producto del catálogo.").showAndWait();return;}int q=qty.getValue();int existing=cart.getItems().stream().filter(it->it.producto().id()==p.id()).mapToInt(CartItem::cantidad).sum();if(existing+q>p.stock()){new Alert(Alert.AlertType.WARNING,"Stock disponible: "+p.stock()+" unidades.").showAndWait();return;}Optional<CartItem> old=cart.getItems().stream().filter(it->it.producto().id()==p.id()).findFirst();if(old.isPresent()){int idx=cart.getItems().indexOf(old.get());cart.getItems().set(idx,new CartItem(p,old.get().cantidad()+q));}else cart.getItems().add(new CartItem(p,q));updateTotal(cart,total);});
        Button remove=secondary("Quitar seleccionado");remove.setOnAction(e->{CartItem item=cart.getSelectionModel().getSelectedItem();if(item!=null)cart.getItems().remove(item);updateTotal(cart,total);});Button clear=secondary("Vaciar carrito");clear.setOnAction(e->{cart.getItems().clear();updateTotal(cart,total);});
        Button confirm=primary("Confirmar venta");confirm.setOnAction(e->run(()->{if(cart.getItems().isEmpty())throw new IllegalArgumentException("Agrega al menos un producto antes de confirmar.");List<VentaService.Linea> lines=cart.getItems().stream().map(it->new VentaService.Linea(it.producto().id(),it.cantidad())).toList();BigDecimal amount=cart.getItems().stream().map(CartItem::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add);int id=ventas.vender(usuario,lines);Alert done=new Alert(Alert.AlertType.INFORMATION,"Venta #"+id+" registrada.\nTotal: S/ "+amount.toPlainString()+"\nEl inventario se actualizó correctamente.");done.setHeaderText("Venta completada");done.showAndWait();cart.getItems().clear();available.clear();available.addAll(dao.listar());filter.run();updateTotal(cart,total);}));
        VBox left=card();left.getChildren().addAll(new Label("1. Catálogo de productos"),new HBox(8,search,category),catalog,new HBox(8,new Label("Cantidad"),qty,add));VBox right=card();right.getChildren().addAll(new Label("2. Resumen de la venta"),cart,new HBox(8,remove,clear),total,confirm);HBox layout=new HBox(14,left,right);HBox.setHgrow(left,Priority.ALWAYS);HBox.setHgrow(right,Priority.ALWAYS);left.setPrefWidth(560);right.setPrefWidth(520);v.getChildren().add(layout);return v;
    }
    private void updateTotal(TableView<CartItem> cart,Label total){BigDecimal sum=cart.getItems().stream().map(CartItem::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add);total.setText("Total: S/ "+sum.setScale(2,java.math.RoundingMode.HALF_UP).toPlainString());}

    private Node reportes() throws Exception {
        VBox v=page("Historial y reportes","Consulta las ventas registradas y exporta el resumen a Excel.");DatePicker from=new DatePicker(LocalDate.now().withDayOfMonth(1));DatePicker to=new DatePicker(LocalDate.now());TextField userFilter=new TextField();userFilter.setPromptText("Filtrar por usuario (opcional)");Button search=primary("Aplicar filtros");Button reset=secondary("Este mes");
        TableView<String[]> t=new TableView<>();String[] cols={"N.º venta","Fecha y hora","Usuario","Total"};for(int i=0;i<cols.length;i++){final int idx=i;TableColumn<String[],String> c=new TableColumn<>(cols[i]);c.setCellValueFactory(x->new SimpleStringProperty(x.getValue()[idx]));c.setPrefWidth(new double[]{95,220,150,120}[i]);t.getColumns().add(c);}t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);t.setPrefHeight(330);
        Label count=new Label();Label sum=new Label();sum.setStyle("-fx-font-size:21px;-fx-font-weight:800;-fx-text-fill:"+GREEN+";");
        Runnable load=()->run(()->{t.getItems().clear();StringBuilder sql=new StringBuilder("SELECT id,fecha,usuario,total FROM ventas WHERE CAST(fecha AS DATE)>=? AND CAST(fecha AS DATE)<=?");if(!userFilter.getText().isBlank())sql.append(" AND LOWER(usuario) LIKE ?");sql.append(" ORDER BY id DESC");BigDecimal total=BigDecimal.ZERO;try(Connection c=Database.open();PreparedStatement p=c.prepareStatement(sql.toString())){p.setDate(1,java.sql.Date.valueOf(from.getValue()));p.setDate(2,java.sql.Date.valueOf(to.getValue()));if(!userFilter.getText().isBlank())p.setString(3,"%"+userFilter.getText().trim().toLowerCase()+"%");try(ResultSet r=p.executeQuery()){while(r.next()){String amount=r.getBigDecimal("total").toPlainString();t.getItems().add(new String[]{r.getString("id"),r.getString("fecha"),r.getString("usuario"),"S/ "+amount});total=total.add(new BigDecimal(amount));}}}count.setText("Ventas encontradas: "+t.getItems().size());sum.setText("Total vendido: S/ "+total.setScale(2,java.math.RoundingMode.HALF_UP).toPlainString());});
        search.setOnAction(e->{if(from.getValue()==null||to.getValue()==null||from.getValue().isAfter(to.getValue())){new Alert(Alert.AlertType.WARNING,"Selecciona un rango de fechas válido.").showAndWait();return;}load.run();});reset.setOnAction(e->{from.setValue(LocalDate.now().withDayOfMonth(1));to.setValue(LocalDate.now());userFilter.clear();load.run();});
        Button export=secondary("Exportar a Excel (.xlsx)");export.setOnAction(e->run(()->{if(t.getItems().isEmpty())throw new IllegalArgumentException("No hay ventas para exportar con los filtros actuales.");FileChooser f=new FileChooser();f.setInitialFileName("reporte_cafegest.xlsx");f.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)","*.xlsx"));File file=f.showSaveDialog(stage);if(file==null)return;exportarVentasExcel(t.getItems(),cols,from.getValue(),to.getValue(),userFilter.getText().trim(),file);new Alert(Alert.AlertType.INFORMATION,"Reporte exportado correctamente.").showAndWait();}));
        VBox filters=card();filters.getChildren().addAll(new Label("Filtros de consulta"),new HBox(10,new VBox(5,subtitle("Desde"),from),new VBox(5,subtitle("Hasta"),to),new VBox(5,subtitle("Usuario"),userFilter),search,reset));VBox summary=card();summary.getChildren().addAll(count,sum);VBox history=card();history.getChildren().addAll(new Label("Detalle de ventas"),t,new HBox(export));v.getChildren().addAll(filters,summary,history);load.run();return v;
    }
    private void exportarVentasExcel(List<String[]> ventasExportadas,String[] columnas,LocalDate desde,LocalDate hasta,String usuarioFiltrado,File archivo)throws Exception{
        try(XSSFWorkbook wb=new XSSFWorkbook();FileOutputStream out=new FileOutputStream(archivo)){
            Sheet hoja=wb.createSheet("Ventas");
            Font tituloFont=wb.createFont();tituloFont.setBold(true);tituloFont.setFontHeightInPoints((short)16);tituloFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle tituloStyle=wb.createCellStyle();tituloStyle.setFont(tituloFont);tituloStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());tituloStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row titulo=hoja.createRow(0);titulo.setHeightInPoints(28);org.apache.poi.ss.usermodel.Cell tituloCelda=titulo.createCell(0);tituloCelda.setCellValue("CaféGest | Reporte de ventas");tituloCelda.setCellStyle(tituloStyle);hoja.addMergedRegion(new CellRangeAddress(0,0,0,columnas.length-1));
            Row filtros=hoja.createRow(1);String detalleFiltros="Periodo: "+desde+" al "+hasta+(usuarioFiltrado.isBlank()?"":" | Usuario: "+usuarioFiltrado);filtros.createCell(0).setCellValue(detalleFiltros);hoja.addMergedRegion(new CellRangeAddress(1,1,0,columnas.length-1));
            Font encabezadoFont=wb.createFont();encabezadoFont.setBold(true);encabezadoFont.setColor(IndexedColors.DARK_GREEN.getIndex());
            CellStyle encabezadoStyle=wb.createCellStyle();encabezadoStyle.setFont(encabezadoFont);encabezadoStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());encabezadoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row encabezado=hoja.createRow(2);for(int i=0;i<columnas.length;i++){org.apache.poi.ss.usermodel.Cell celda=encabezado.createCell(i);celda.setCellValue(columnas[i]);celda.setCellStyle(encabezadoStyle);}
            CellStyle fechaStyle=wb.createCellStyle();fechaStyle.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
            CellStyle montoStyle=wb.createCellStyle();montoStyle.setDataFormat(wb.createDataFormat().getFormat("\"S/ \"#,##0.00"));
            int fila=3;
            for(String[] venta:ventasExportadas){
                Row registro=hoja.createRow(fila++);
                registro.createCell(0).setCellValue(Long.parseLong(venta[0]));
                org.apache.poi.ss.usermodel.Cell fecha=registro.createCell(1);fecha.setCellValue(Timestamp.valueOf(venta[1]));fecha.setCellStyle(fechaStyle);
                registro.createCell(2).setCellValue(venta[2]);
                org.apache.poi.ss.usermodel.Cell monto=registro.createCell(3);monto.setCellValue(new BigDecimal(venta[3].replace("S/ ","")).doubleValue());monto.setCellStyle(montoStyle);
            }
            int ultimaFilaDatos=fila-1;
            hoja.setAutoFilter(new CellRangeAddress(2,ultimaFilaDatos,0,columnas.length-1));hoja.createFreezePane(0,3);
            Font totalFont=wb.createFont();totalFont.setBold(true);
            CellStyle totalStyle=wb.createCellStyle();totalStyle.setFont(totalFont);
            Row total=hoja.createRow(fila);org.apache.poi.ss.usermodel.Cell etiquetaTotal=total.createCell(0);etiquetaTotal.setCellValue("Total de ventas");etiquetaTotal.setCellStyle(totalStyle);hoja.addMergedRegion(new CellRangeAddress(fila,fila,0,2));
            org.apache.poi.ss.usermodel.Cell suma=total.createCell(3);suma.setCellFormula("SUM(D4:D"+(ultimaFilaDatos+1)+")");suma.setCellStyle(montoStyle);
            for(int i=0;i<columnas.length;i++){hoja.autoSizeColumn(i);hoja.setColumnWidth(i,Math.min(hoja.getColumnWidth(i)+512,40*256));}
            wb.write(out);
        }
    }
    private interface Action { void run() throws Exception; }
    private void run(Action a){try{a.run();}catch(Exception e){error(e);}}
    private void error(Exception e){new Alert(Alert.AlertType.ERROR,e.getMessage()==null?e.toString():e.getMessage()).showAndWait();}
    public static void main(String[] args){launch(args);}
}
