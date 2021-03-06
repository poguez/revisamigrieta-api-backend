package com.revisamigrieta.backend;

import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.*;
import com.revisamigrieta.backend.models.*;
import com.revisamigrieta.backend.models.dao.GrietaDao;
import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.UnauthorizedException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.revisamigrieta.backend.helpers.Constants.API_VERSION;

/**
 * The GrietaEndpoint API which Endpoints will be exposing.
 */
// [START grieta_api_annotation]
@Api(
		name = "grieta",
		version = "v1",
		namespace =
		@ApiNamespace(
				ownerDomain = "backend.revisamigrieta.backend",
				ownerName = "backend.revisamigrieta.backend",
				packagePath = ""
		),
		// [START_EXCLUDE]
		issuers = {
				@ApiIssuer(
						name = "firebase",
						issuer = "https://securetoken.google.com/revisamigrieta",
						jwksUri = "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system.gserviceaccount.com")
		},
		issuerAudiences = {
				@ApiIssuerAudience(name = "firebase", audiences = "revisamigrieta")
		}
		// [END_EXCLUDE]
)
// [END grieta_api_annotation]
public class GrietaEndpoint {
	private static final Logger logger = Logger.getLogger(GrietaEndpoint.class.getName());

	// [START publish_method]
	@ApiMethod(
			name = "publish",
			path = API_VERSION + "/grietas",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = {EspAuthenticator.class},
			issuerAudiences = {@ApiIssuerAudience(name = "firebase",
					audiences = {"revisamigrieta"})}
	)
	public void publish(User user,
	                    @Named("mas20porciento") boolean mas20porciento,
	                    @Named("comentario") String comentario,
	                    @Named("desplomes") boolean desplomes,
	                    @Named("desprendimiento") boolean desprendimiento,
	                    @Named("golpeteo") boolean golpeteo,
	                    @Named("hundimiento") boolean hundimiento,
	                    @Named("pisosHuecos") boolean pisosHuecos,
	                    @Named("tweet") String tweet,
	                    @Named("vibraciones") boolean vibraciones,
	                    @Named("latitude") float latitude,
	                    @Named("longitude") float longitude,
	                    @Named("tipo") int tipo,
	                    @Named("diagonales") boolean diagonales,
	                    @Named("paralelas") boolean paralelas,
	                    @Named("ubicacion") int ubicacion,
	                    @Named("filesId") String filesId
	) throws UnauthorizedException, NotFoundException {
		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
		GrietaDao grietaDao = new GrietaDao();
		GrietaModel grietaModel = new GrietaModel();

		EstadoDeObra estadoDeObra = new EstadoDeObra();
		estadoDeObra.setMas20porciento(mas20porciento);
		estadoDeObra.setDesplomes(desplomes);
		estadoDeObra.setDesprendimiento(desprendimiento);
		estadoDeObra.setGolpeteo(golpeteo);
		estadoDeObra.setHundimientos(hundimiento);
		estadoDeObra.setPisosHuecos(pisosHuecos);
		estadoDeObra.setVibraciones(vibraciones);
		grietaModel.setEstadoDeObra(estadoDeObra);

		grietaModel.setUserId(user.getId());
		grietaModel.setComentario(comentario);
		grietaModel.setTipo(TipoEnum.values()[tipo]);
		grietaModel.setTweet(tweet);
		grietaModel.setUbicacion(UbicacionEnum.values()[tipo]);
		grietaModel.setGeolocalizacion(new GeoPt(latitude, longitude));

		if(grietaModel.getUbicacion() == UbicacionEnum.LOSA){
			grietaModel.setDiagonalesLosa(diagonales);
		} else if(grietaModel.getUbicacion() == UbicacionEnum.PISO) {
			grietaModel.setDiagonalesPiso(diagonales);
			grietaModel.setParalelasPiso(paralelas);
		} else{
			throw new UnauthorizedException("Tipo de grieta requerido.");
		}




		grietaDao.put(grietaModel);

	}
	// [END publish_method]

	// [START retrieveAllGrietas_method]
	@ApiMethod(name = "retrieveAllGrietas", path = API_VERSION + "/grietas", httpMethod = ApiMethod.HttpMethod.GET)
	public List<GrietaModel> retrieveAllGrietas() {

		GrietaDao grietaDao = new GrietaDao();
		List<GrietaModel> grietaModelList = grietaDao.getAll();
		logger.info(grietaModelList.toString());
		return grietaModelList;

	}
	// [END retrieveAllGrietas_method]

	// [START retrieveAllGrietas_method]
	@ApiMethod(name = "retrieveAllPendingGrietas", path = API_VERSION + "/grietas/pending", httpMethod = ApiMethod.HttpMethod.GET)
	public List<GrietaModel> retrieveAllPendingGrietas() {

		GrietaDao grietaDao = new GrietaDao();
		Query.Filter pendingFilter =
				new Query.FilterPredicate("revisada", Query.FilterOperator.EQUAL, false);

		ArrayList<Query.Filter> filters = new ArrayList<>();
		filters.add(pendingFilter);

		List<GrietaModel> grietaModelList = grietaDao.getWithFilters(filters);
		logger.info(grietaModelList.toString());
		return grietaModelList;

	}
	// [END retrieveAllGrietas_method]


	// [START retrieveGrieta_method]
	@ApiMethod(name = "retrieveGrietas", path = API_VERSION + "/grietas/{id}", httpMethod = ApiMethod.HttpMethod.GET)
	public GrietaModel retrieveGrietas(@Named("id") String id) {
		Long grietaId = Long.parseLong(id);
		GrietaDao grietaDao = new GrietaDao();

		GrietaModel grietaModel = grietaDao.get(grietaId);

		logger.info(grietaModel.toString());
		return grietaModel;
	}
	// [END retrieveGrieta_method]




}
