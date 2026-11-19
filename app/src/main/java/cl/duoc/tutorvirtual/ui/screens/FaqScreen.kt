package cl.duoc.tutorvirtual.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cl.duoc.tutorvirtual.ui.theme.LocalColoresEsucar

import java.text.Normalizer
import java.util.Locale

// MODELO DE PREGUNTA FRECUENTE
private data class PreguntaFaq(
    val id: Int,
    val categoria: String,
    val pregunta: String,
    val respuesta: String
)

// BÚSQUEDA SIN DIFERENCIAR MAYÚSCULAS NI TILDES
private fun normalizarFaq(texto: String): String {
    return Normalizer.normalize(
        texto.lowercase(Locale.ROOT),
        Normalizer.Form.NFD
    ).replace(
        Regex("\\p{M}+"),
        ""
    )
}

// CONTENIDO DE LA GUÍA METODOLÓGICA (PREGUNTAS FRECUENTES POR CAPÍTULO)
private val preguntasFaq = listOf(
    PreguntaFaq(
        1,
        "Planteamiento",
        "¿Qué es el planteamiento del problema?",
        "Es la exposición argumentada de una situación que " +
                "requiere ser conocida o explicada. Va de lo general " +
                "a lo específico: contexto, evidencia del problema, " +
                "vacío de conocimiento y pregunta de investigación."
    ),
    PreguntaFaq(
        2,
        "Planteamiento",
        "¿Qué apartados lleva normalmente?",
        "Antecedentes o contextualización, descripción del " +
                "problema, pregunta(s) de investigación, objetivos " +
                "(general y específicos), justificación, delimitación " +
                "y, según el enfoque, hipótesis o supuestos. Revisa " +
                "el orden exacto en la pauta de tu carrera."
    ),
    PreguntaFaq(
        3,
        "Planteamiento",
        "¿Cuál es la diferencia entre un tema y un problema?",
        "Un tema es un área amplia (accidentes de tránsito). " +
                "Un problema es una situación concreta, en un lugar, " +
                "tiempo y población, que se desconoce o no se explica " +
                "bien (no se sabe cómo se relaciona el consumo de " +
                "alcohol con la ocurrencia de los accidentes de " +
                "tránsito en la ciudad de Antofagasta)."
    ),
    PreguntaFaq(
        4,
        "Planteamiento",
        "¿Cómo sé si mi problema es investigable?",
        "Debe poder estudiarse con datos que realmente puedes " +
                "obtener, en el tiempo disponible, con recursos a tu " +
                "alcance y sin conflictos éticos. Si no puedes " +
                "imaginar qué dato respondería la pregunta, aún no es " +
                "investigable."
    ),
    PreguntaFaq(
        5,
        "Planteamiento",
        "¿Qué debo incluir en los antecedentes?",
        "Cifras, estudios previos, normativa o hechos que " +
                "muestren que el problema existe y es relevante. Usa " +
                "fuentes recientes (idealmente de los últimos 5 a 10 " +
                "años) y avanza del contexto internacional o nacional " +
                "al local."
    ),
    PreguntaFaq(
        6,
        "Planteamiento",
        "¿Cómo demuestro que el problema existe y no es solo " +
                "una opinión mía?",
        "Con evidencia citada: estadísticas oficiales, " +
                "informes, investigaciones previas o diagnósticos " +
                "institucionales. Cada afirmación importante debe " +
                "tener una referencia."
    ),
    PreguntaFaq(
        7,
        "Planteamiento",
        "¿Qué es el vacío de conocimiento y cómo lo " +
                "identifico?",
        "Es lo que la literatura aún no responde: un contexto " +
                "no estudiado, una población distinta, resultados " +
                "contradictorios o un enfoque no aplicado. Se " +
                "identifica revisando estudios previos y anotando qué " +
                "recomiendan investigar a futuro."
    ),
    PreguntaFaq(
        8,
        "Planteamiento",
        "¿Debo mencionar causas y consecuencias del problema?",
        "Sí, brevemente. Un árbol de problemas (causas abajo, " +
                "problema central, efectos arriba) ayuda a ordenarlas " +
                "y a decidir qué parte abordarás."
    ),
    PreguntaFaq(
        9,
        "Planteamiento",
        "¿Qué extensión debe tener la descripción del " +
                "problema?",
        "Depende de la pauta y de las condiciones de formato " +
                "que disponga la casa de estudios, pero suele ocupar " +
                "entre 3 y 6 páginas. Importa más que cada párrafo " +
                "conduzca a la pregunta que la cantidad de páginas."
    ),
    PreguntaFaq(
        10,
        "Planteamiento",
        "¿Cómo formulo una buena pregunta de investigación?",
        "Incluye qué se estudia (variables o fenómeno), en " +
                "quiénes (población), dónde (contexto) y cuándo " +
                "(período). Ejemplo: ¿Qué relación existe entre el " +
                "consumo de alcohol y la ocurrencia de accidentes de " +
                "tránsito en la Zona de Carabineros Antofagasta " +
                "durante los años 2020 al 2026?"
    ),
    PreguntaFaq(
        11,
        "Planteamiento",
        "¿Puedo tener más de una pregunta?",
        "Sí: una pregunta general y preguntas específicas o " +
                "secundarias. Las específicas suelen corresponder una " +
                "a una con los objetivos específicos."
    ),
    PreguntaFaq(
        12,
        "Planteamiento",
        "¿Qué errores son comunes en la pregunta?",
        "Preguntas de sí o no, demasiado amplias, que ya " +
                "tienen respuesta conocida, que contienen juicios de " +
                "valor (¿por qué es malo…?) o que no se pueden " +
                "responder con datos."
    ),
    PreguntaFaq(
        13,
        "Planteamiento",
        "¿Cómo redacto el objetivo general?",
        "Con una fórmula que debe considerar verbo en " +
                "infinitivo (que exprese el logro final del estudio, " +
                "alineado con la pregunta general, ejemplo: Analizar, " +
                "Determinar, Describir, Comprender, Evaluar. Debe ser " +
                "uno solo); el objeto de estudio (es fenómeno que se " +
                "estudia, por ejemplo: el consumo de alcohol como " +
                "causa de los accidentes de tránsito); unidad de " +
                "análisis (sujetos que serán el foco de estudio, " +
                "pudiendo ser documentos, personas o grupos); " +
                "delimitación de espacio (que es el territorio en que " +
                "se circunscribe el estudio. Ejemplo: comuna, región, " +
                "un sector poblacional, macrozona, entre otros) y " +
                "delimitación de tiempo (que es el período en el que " +
                "se aborda el estudio, pudiendo ser de un período " +
                "extenso o específico. Ejemplo: del 2020 al 2026; " +
                "durante el año 2025, primer semestre del año 2026, " +
                "los último 10 años, entre otros)"
    ),
    PreguntaFaq(
        14,
        "Planteamiento",
        "¿Cuántos objetivos específicos necesito y cómo se " +
                "relacionan con el general?",
        "Habitualmente entre 3 y 5 por objetivo general. Son " +
                "los pasos que, sumados, permiten cumplir el general. " +
                "Si los cumples todos, el general queda logrado."
    ),
    PreguntaFaq(
        15,
        "Planteamiento",
        "¿Qué verbos evito en los objetivos?",
        "Verbos de acción operativa o no verificables: " +
                "conocer, entender, aplicar una encuesta, realizar " +
                "entrevistas, mejorar, implementar (salvo que la " +
                "tesis sea de intervención como investigación - " +
                "acción o sea un proyecto). Aplicar una encuesta es " +
                "una actividad, no un objetivo. Las etapas del " +
                "proceso tampoco son objetivos (ejemplo: analizar los " +
                "resultados o recoger información)"
    ),
    PreguntaFaq(
        16,
        "Planteamiento",
        "¿El nivel del verbo debe coincidir con el alcance " +
                "del estudio?",
        "Sí. Describir o identificar corresponde a un alcance " +
                "descriptivo; relacionar o determinar la asociación, " +
                "a uno correlacional; explicar o determinar el " +
                "efecto, a uno explicativo; comprender o interpretar, " +
                "a estudios cualitativos. Debe haber una " +
                "correspondencia pertinente entre el verbo y el " +
                "alcance del estudio."
    ),
    PreguntaFaq(
        17,
        "Planteamiento",
        "¿Qué criterios uso para justificar?",
        "Conveniencia (para qué sirve), relevancia social (a " +
                "quién beneficia), implicancias prácticas (qué " +
                "problema ayuda a resolver), valor teórico (qué " +
                "aporta al conocimiento) y utilidad metodológica (si " +
                "crea o valida un instrumento)."
    ),
    PreguntaFaq(
        18,
        "Planteamiento",
        "¿En qué se diferencia la justificación de los " +
                "antecedentes?",
        "Los antecedentes muestran que el problema existe; la " +
                "justificación explica por qué vale la pena " +
                "estudiarlo y quién se beneficia con los resultados."
    ),
    PreguntaFaq(
        19,
        "Planteamiento",
        "¿Debo explicar la viabilidad?",
        "Sí, este aspecto es altamente relevante dado que es " +
                "un criterio para saber si será posible concretar el " +
                "proceso: se mide analizando el acceso a la " +
                "población, permisos institucionales, tiempo de " +
                "recolección v/s tiempo asignado por el calendario " +
                "académico, recursos de todo tipo (logístico, " +
                "institucionales, humanos, tecnológicos) y " +
                "competencias del investigador (nivel de experticia " +
                "v/s complejidad del estudio)"
    ),
    PreguntaFaq(
        20,
        "Planteamiento",
        "¿Qué es la delimitación?",
        "Los límites del estudio: espacial (dónde), temporal " +
                "(cuándo), poblacional (quiénes) y temática (qué " +
                "aspectos sí y cuáles no se abordan)."
    ),
    PreguntaFaq(
        21,
        "Planteamiento",
        "¿Delimitación y limitaciones son lo mismo?",
        "No. La delimitación es una decisión tuya sobre el " +
                "alcance; las limitaciones son restricciones que " +
                "afectan los resultados (muestra pequeña, acceso " +
                "parcial) y suelen tratarse en metodología o " +
                "conclusiones."
    ),
    PreguntaFaq(
        22,
        "Planteamiento",
        "¿La investigación necesita hipótesis?",
        "Las necesitan los estudios cuantitativos " +
                "correlacionales, explicativos o experimentales. Los " +
                "descriptivos pueden no tenerlas y los cualitativos " +
                "no las formulan al inicio; en su lugar usan " +
                "supuestos o preguntas directrices."
    ),
    PreguntaFaq(
        23,
        "Planteamiento",
        "¿Cómo redacto una hipótesis?",
        "Como una afirmación verificable que relaciona " +
                "variables. Ejemplo: “A mayor consumo de alcohol " +
                "mayor probabilidad de generar accidente de " +
                "tránsito”. Se acompaña de su hipótesis nula cuando " +
                "se hará prueba estadística."
    ),
    PreguntaFaq(
        24,
        "Planteamiento",
        "¿Cómo verifico la coherencia del capítulo?",
        "Con una matriz de consistencia: una tabla con " +
                "problema, pregunta general y específicas, objetivo " +
                "general y específicos, hipótesis o supuestos y " +
                "variables o categorías. Cada fila debe conectar con " +
                "la siguiente."
    ),
    PreguntaFaq(
        25,
        "Planteamiento",
        "¿Puedo cambiar el planteamiento después?",
        "Sí, y es frecuente ajustarlo tras revisar la " +
                "literatura o recoger datos. Si lo cambia, debe " +
                "preocuparse de revisar que objetivos, metodología y " +
                "conclusiones se modifiquen si es necesario para " +
                "seguir alineados."
    ),
    PreguntaFaq(
        26,
        "Planteamiento",
        "¿Qué suele preguntar la comisión sobre este " +
                "capítulo?",
        "Por qué elegiste el problema, cuál es el aporte " +
                "nuevo, por qué esa población y no otra, y si los " +
                "objetivos se cumplieron realmente."
    ),
    PreguntaFaq(
        27,
        "Marco teórico",
        "¿Para qué sirve el marco teórico?",
        "Para situar el problema en el conocimiento " +
                "existente, evitar repetir estudios, orientar la " +
                "metodología, entregar los conceptos que se " +
                "operacionalizarán y ofrecer un referente para " +
                "interpretar los resultados."
    ),
    PreguntaFaq(
        28,
        "Marco teórico",
        "¿Qué partes suele tener?",
        "Antecedentes investigativos o estado del arte (qué " +
                "existe acerca del tema ya en la comunidad " +
                "científica), bases teóricas (teorías y enfoques con " +
                "sus respectivos postulados, autores, explicación de " +
                "su uso), marco conceptual (definiciones clave) y, si " +
                "corresponde, marco legal o normativo y marco " +
                "contextual o institucional."
    ),
    PreguntaFaq(
        29,
        "Marco teórico",
        "¿Cómo organizo los capítulos internos?",
        "Por variables o categorías del estudio, no por " +
                "autores. Un esquema útil: un apartado por cada " +
                "variable o dimensión, y un apartado final sobre la " +
                "relación entre ellas. Considerar como subtítulos " +
                "para organizar de mejor forma el capítulo. (Ejemplo: " +
                "marco referencial, marco teórico y marco " +
                "conceptual)."
    ),
    PreguntaFaq(
        30,
        "Marco teórico",
        "¿Qué diferencia hay entre marco teórico, conceptual, " +
                "referencial y estado del arte?",
        "El marco teórico expone teorías que explican el " +
                "fenómeno; el conceptual define los términos clave; " +
                "el referencial es un nombre amplio que agrupa todo " +
                "lo anterior; el estado del arte resume lo que se ha " +
                "investigado recientemente sobre el tema. Usa la " +
                "nomenclatura de tu pauta."
    ),
    PreguntaFaq(
        31,
        "Marco teórico",
        "¿Dónde busco fuentes confiables?",
        "Bases académicas como Scopus, Web of Science, " +
                "SciELO, Redalyc, Dialnet, ERIC o PubMed según el " +
                "área, Google Académico, repositorios de tesis (por " +
                "ejemplo, el repositorio de la propia casa de " +
                "estudios), normativa oficial de la institución " +
                "(reglamentos, circulares, protocolos, órdenes " +
                "generales, manuales, instructivos, entre otros), " +
                "normativa legal (leyes, decretos, decretos con " +
                "fuerza de ley, carta fundamental, tratados " +
                "internacionales, etc.) y organismos oficiales (INE, " +
                "ministerios, OCDE, CEPAL, UNESCO, ONU, UNICEF, OMS, " +
                "FLACSO, OIT, etc.)."
    ),
    PreguntaFaq(
        32,
        "Marco teórico",
        "¿Qué fuentes evito?",
        "Páginas sin autoría, blogs, Wikipedia como fuente " +
                "final, monografías de sitios comerciales y textos " +
                "sin fecha. Pueden orientar la búsqueda, pero no se " +
                "citan como sustento."
    ),
    PreguntaFaq(
        33,
        "Marco teórico",
        "¿Qué tan recientes deben ser las fuentes?",
        "Para el estado del arte, preferentemente de los " +
                "últimos 5 a 10 años. Las teorías clásicas pueden ser " +
                "antiguas, pero conviene acompañarlas de autores " +
                "actuales que las apliquen."
    ),
    PreguntaFaq(
        34,
        "Marco teórico",
        "¿Cuántas referencias necesito?",
        "No hay un número universal; en tesis de pregrado es " +
                "común (pero no excluyente) entre 30 y 60. Importa " +
                "más su pertinencia y calidad que la cantidad. Revisa " +
                "si tu escuela exige un mínimo."
    ),
    PreguntaFaq(
        35,
        "Marco teórico",
        "¿Cómo organizo lo que leo?",
        "Con una matriz bibliográfica (autor, año, objetivo, " +
                "metodología, resultados, aporte a mi trabajo de " +
                "título) y un gestor de referencias como Zotero o " +
                "Mendeley, que también genera las citas."
    ),
    PreguntaFaq(
        36,
        "Marco teórico",
        "¿Qué estudios incluyo como antecedentes?",
        "Investigaciones empíricas con objetivos, variables o " +
                "poblaciones similares a las tuyas, idealmente " +
                "internacionales, nacionales y locales. Siempre " +
                "organizadas desde lo general (global internacional) " +
                "hasta lo particular (local o institucional)"
    ),
    PreguntaFaq(
        37,
        "Marco teórico",
        "¿Qué información presento de cada antecedente?",
        "Para efectos de los estudios referenciales o estados " +
                "del arte, señalar autor y año, objetivo, metodología " +
                "(enfoque, muestra, instrumento), principales " +
                "resultados y en qué se relaciona o diferencia de tu " +
                "estudio."
    ),
    PreguntaFaq(
        38,
        "Marco teórico",
        "¿Cómo evito que sea una lista de resúmenes?",
        "Agrupa los estudios por coincidencias o diferencias, " +
                "compáralos y cierra cada bloque con una síntesis " +
                "propia: qué se sabe, qué se contradice y qué falta."
    ),
    PreguntaFaq(
        39,
        "Marco teórico",
        "¿Cómo elijo la teoría que sustenta mi trabajo?",
        "Prefiere la que mejor explique la relación o " +
                "fenómeno que estudias y que se use en " +
                "investigaciones similares. Justifica por qué la " +
                "eliges frente a otras. Refiérala con su título, " +
                "postulado, autor, área del conocimiento desde la " +
                "cual emana (psicología, sociología, educación, " +
                "antropología, criminología, etc.); año de su " +
                "publicación, postulado breve y justificación de su " +
                "elección en relación a la vinculación que tiene con " +
                "su problema de investigación."
    ),
    PreguntaFaq(
        40,
        "Marco teórico",
        "¿Debo presentar teorías en contraposición?",
        "Es recomendable mostrar distintas perspectivas y " +
                "luego declarar cuál adoptas. Eso muestra dominio del " +
                "tema."
    ),
    PreguntaFaq(
        41,
        "Marco teórico",
        "¿Cómo defino los conceptos clave?",
        "Presenta dos o tres definiciones de autores " +
                "reconocidos, compáralas y termina con la definición " +
                "que usarás en tu estudio. Esa definición es la base " +
                "de la operacionalización."
    ),
    PreguntaFaq(
        42,
        "Marco teórico",
        "¿Qué relación tiene el marco teórico con la " +
                "operacionalización de variables?",
        "Existe una relación directa entre estos elementos, " +
                "ya que las dimensiones e indicadores de cada " +
                "variable deben salir de las teorías y definiciones " +
                "del marco. Si una dimensión no tiene sustento " +
                "teórico, falta revisar literatura."
    ),
    PreguntaFaq(
        43,
        "Marco teórico",
        "¿Incluyo marco legal o contextual?",
        "Esto depende de lo que solicita el formato o pauta " +
                "del estudio y de si el tema tiene relación o " +
                "sustento en alguna normativa que regula el fenómeno " +
                "(leyes, políticas públicas) o características de la " +
                "institución o territorio que ayudan a interpretar " +
                "los resultados."
    ),
    PreguntaFaq(
        44,
        "Marco teórico",
        "¿Cómo integro las citas sin copiar?",
        "Parafrasea con tus palabras y cita la fuente. " +
                "Reserva las citas textuales para definiciones o " +
                "ideas cuya redacción exacta importa, y no encadenes " +
                "varias seguidas. Es importante diferenciar el " +
                "parafraseo de la cita textual, dado que implican una " +
                "forma distinta de evidenciarlo en el documento según " +
                "el formato de la norma que regule los estándares de " +
                "escritura y presentación de trabajos académicos y " +
                "científicos."
    ),
    PreguntaFaq(
        45,
        "Marco teórico",
        "¿Qué norma de citación uso?",
        "En ESUCAR se utiliza la norma APA 7.ª edición. " +
                "Mantén la misma norma en todo el documento y en la " +
                "lista de referencias."
    ),
    PreguntaFaq(
        46,
        "Marco teórico",
        "¿Qué es el plagio y cómo lo evito?",
        "Es presentar ideas o textos ajenos como propios, " +
                "incluida la paráfrasis sin cita. Se evita citando " +
                "toda idea ajena y revisando el texto con el software " +
                "antiplagio que use tu casa de estudios. ESUCAR " +
                "utiliza TURNITIN."
    ),
    PreguntaFaq(
        47,
        "Marco teórico",
        "¿Puedo usar inteligencia artificial para redactar?",
        "En general puede apoyar la búsqueda o la revisión de " +
                "estilo, pero las fuentes deben verificarse una por " +
                "una y el análisis debe ser tuyo; declara su uso si " +
                "la normativa lo exige. Es importante revisar la " +
                "normativa interna al respecto."
    ),
    PreguntaFaq(
        48,
        "Marco teórico",
        "¿Cuánto debe medir el capítulo?",
        "Suele ser el más extenso de la tesis, por lo general " +
                "entre 15 y 30 páginas en pregrado. Recorta todo lo " +
                "que no se relacione con tus variables u objetivos."
    ),
    PreguntaFaq(
        49,
        "Marco teórico",
        "¿Cómo cierro el marco teórico?",
        "Con una síntesis que conecte las teorías con tu " +
                "problema y, si aplica, un esquema o mapa conceptual " +
                "de las variables y sus relaciones."
    ),
    PreguntaFaq(
        50,
        "Marco teórico",
        "¿Qué suele preguntar la comisión sobre este " +
                "capítulo?",
        "Cuál es tu postura teórica, por qué elegiste esos " +
                "autores, cómo definiste cada concepto y cómo esas " +
                "definiciones se reflejan en tus instrumentos."
    ),
    PreguntaFaq(
        51,
        "Metodología",
        "¿Qué enfoque elijo: cuantitativo, cualitativo o " +
                "mixto?",
        "Cuantitativo si quieres medir, comparar o relacionar " +
                "variables con datos numéricos. Cualitativo si buscas " +
                "comprender significados, experiencias o procesos " +
                "desde la perspectiva de los participantes. Mixto si " +
                "necesitas ambas miradas y puedes integrarlas."
    ),
    PreguntaFaq(
        52,
        "Metodología",
        "¿Qué es el paradigma y debo declararlo?",
        "Es la postura sobre cómo se conoce la realidad: " +
                "positivista o pospositivista (cuantitativo), " +
                "interpretativo o constructivista (cualitativo), " +
                "sociocrítico (investigación-acción) o pragmático " +
                "(mixto). Muchas pautas lo piden; decláralo en un " +
                "párrafo justificando su selección en relación al " +
                "tema y el propósito de tu trabajo."
    ),
    PreguntaFaq(
        53,
        "Metodología",
        "¿Qué es el alcance o nivel de la investigación?",
        "La profundidad con que se aborda el problema: " +
                "exploratorio (tema poco estudiado), descriptivo " +
                "(caracterizar), correlacional (asociar variables) o " +
                "explicativo (establecer causas). Debe coincidir con " +
                "los verbos de los objetivos."
    ),
    PreguntaFaq(
        54,
        "Metodología",
        "¿Qué diseños existen en el enfoque cuantitativo?",
        "Experimental (se manipula una variable y hay " +
                "asignación aleatoria), cuasi-experimental (se " +
                "manipula sin aleatorización) y no experimental (se " +
                "observa sin intervenir). El no experimental puede " +
                "ser transversal (una medición) o longitudinal " +
                "(varias en el tiempo)."
    ),
    PreguntaFaq(
        55,
        "Metodología",
        "¿Y en el enfoque cualitativo?",
        "Estudio de caso, fenomenológico (experiencias " +
                "vividas), teoría fundamentada (generar teoría desde " +
                "los hallazgos alcanzados en el campo), etnográfico " +
                "(cultura de un grupo), narrativo o biográfico, e " +
                "investigación-acción (transformar una práctica)."
    ),
    PreguntaFaq(
        56,
        "Metodología",
        "¿Y en el enfoque mixto?",
        "Convergente (datos cuanti y cuali al mismo tiempo y " +
                "se comparan), secuencial explicativo (primero " +
                "cuanti, luego cuali para explicar) y secuencial " +
                "exploratorio (primero cuali, luego cuanti para " +
                "generalizar)."
    ),
    PreguntaFaq(
        57,
        "Metodología",
        "¿Qué diferencia hay entre población, muestra y " +
                "unidad de análisis?",
        "La población es el conjunto total de casos que " +
                "cumplen los criterios del estudio (cantidad); la " +
                "muestra es el subconjunto del que se obtienen datos " +
                "(subgrupo que oficina de fuente de recolección de " +
                "datos); la unidad de análisis es quién o qué se " +
                "estudia (concepto referido al sujeto de estudio: " +
                "estudiantes, docentes, documentos, instituciones)."
    ),
    PreguntaFaq(
        58,
        "Metodología",
        "¿Qué tipos de muestreo hay?",
        "Probabilístico (aleatorio simple, sistemático, " +
                "estratificado, por conglomerados), que permite " +
                "generalizar, y no probabilístico (por conveniencia, " +
                "intencionado, por cuotas, bola de nieve), que no " +
                "permite inferir a la población. La tipología está " +
                "relacionada con el enfoque. Debe haber coherencia " +
                "con ello. Ejemplo: un estudio cuantitativo puede " +
                "tener muestra probabilística o no probabilística; " +
                "sin embargo, el estudio cualitativo no puede llevar " +
                "muestra probabilística."
    ),
    PreguntaFaq(
        59,
        "Metodología",
        "¿Cómo calculo el tamaño de muestra en un estudio " +
                "cuantitativo?",
        "Con la fórmula para poblaciones finitas o una " +
                "calculadora en línea, usando nivel de confianza " +
                "(habitualmente 95 %), margen de error (5 %) y tamaño " +
                "de la población. Informa los valores usados. " +
                "Considerar para el cálculo si se trabaja con " +
                "población finita (total de sujetos es conocido o de " +
                "tamaño accesible) o si población infinita (dato " +
                "desconocido o en cantidad poco probable de " +
                "alcanzar)."
    ),
    PreguntaFaq(
        60,
        "Metodología",
        "¿Cuántos participantes necesito en un estudio " +
                "cualitativo?",
        "No se calcula: se busca la saturación teórica, es " +
                "decir, el punto en que nuevas entrevistas ya no " +
                "aportan información nueva. El número exacto será " +
                "pertinente en función del tipo de entrevista. En " +
                "pregrado suelen usarse entre 3 y 15 entrevistas, " +
                "dependiendo del diseño y el tipo de entrevista (si " +
                "es individual o grupal)"
    ),
    PreguntaFaq(
        61,
        "Metodología",
        "¿Qué son los criterios de inclusión y exclusión?",
        "Características que debe cumplir un caso para " +
                "participar (inclusión) y condiciones que lo dejan " +
                "fuera, aunque cumpla las anteriores (exclusión). " +
                "Decláralos de forma explícita. Van directamente " +
                "relacionados con el tipo de muestreo no " +
                "probabilístico."
    ),
    PreguntaFaq(
        62,
        "Metodología",
        "¿Qué es la operacionalización de variables?",
        "Traducir cada variable en algo medible. Se presenta " +
                "en una tabla con variable, definición conceptual " +
                "(del marco teórico), definición operacional, " +
                "dimensiones, indicadores, ítems y escala de " +
                "medición."
    ),
    PreguntaFaq(
        63,
        "Metodología",
        "¿Qué escalas de medición existen y por qué importan?",
        "Nominal, ordinal, intervalar y de razón. Determinan " +
                "qué estadísticos puedes usar: no se calcula un " +
                "promedio de una variable nominal, por ejemplo."
    ),
    PreguntaFaq(
        64,
        "Metodología",
        "¿Qué son las categorías en un estudio cualitativo?",
        "Ejes temáticos que organizan el análisis. Pueden ser " +
                "apriorísticas (definidas desde el marco teórico, con " +
                "subcategorías) o emergentes (surgen de los datos). " +
                "Se presentan en una matriz de categorías."
    ),
    PreguntaFaq(
        65,
        "Metodología",
        "¿Qué diferencia hay entre técnica e instrumento?",
        "La técnica es el procedimiento (encuesta, " +
                "entrevista, observación, análisis documental, grupo " +
                "focal); el instrumento es la herramienta concreta " +
                "(cuestionario, guion de entrevista, pauta de " +
                "observación, ficha de registro)."
    ),
    PreguntaFaq(
        66,
        "Metodología",
        "¿Puedo usar un instrumento ya existente?",
        "Sí, y es recomendable si está validado en una " +
                "población similar. Cita a sus autores, informa su " +
                "confiabilidad original y, si lo adaptas, explica qué " +
                "cambiaste y por qué."
    ),
    PreguntaFaq(
        67,
        "Metodología",
        "¿Cómo construyo un instrumento propio?",
        "A partir de la operacionalización de objetivos que " +
                "se realiza con una matriz de correspondencia, donde " +
                "cada indicador genera uno o más ítems o preguntas. " +
                "Luego se valida y se prueba con un piloto."
    ),
    PreguntaFaq(
        68,
        "Metodología",
        "¿Cómo valido un instrumento?",
        "Por juicio de expertos (validez de contenido, " +
                "habitualmente con 3 a 5 jueces que evalúan claridad, " +
                "pertinencia y coherencia), prueba piloto con " +
                "personas similares a la muestra y, en cuantitativo, " +
                "análisis de confiabilidad."
    ),
    PreguntaFaq(
        69,
        "Metodología",
        "¿Qué es la confiabilidad y cómo la calculo?",
        "Es la consistencia de las mediciones. En escalas " +
                "tipo Likert se usa el alfa de Cronbach; valores " +
                "desde 0,70 suelen considerarse aceptables. Para otro " +
                "tipo de instrumentos hay procedimientos como Kappa " +
                "de Cohen, Kappa de Fleiss, Alfa de Krippendorff, " +
                "Coeficiente de Correlación Intraclase (CCI), etc."
    ),
    PreguntaFaq(
        70,
        "Metodología",
        "¿Cómo aseguro la calidad en un estudio cualitativo?",
        "Con criterios de rigor: credibilidad (triangulación, " +
                "devolución a participantes), transferibilidad " +
                "(descripción densa del contexto), dependencia " +
                "(registro del proceso) y confirmabilidad " +
                "(reflexividad del investigador)."
    ),
    PreguntaFaq(
        71,
        "Metodología",
        "¿Qué debo describir en el procedimiento?",
        "Los pasos en orden: solicitud de permisos, contacto " +
                "con participantes, firma de consentimientos, " +
                "aplicación del instrumento (modalidad, lugar, " +
                "duración), registro y resguardo de los datos."
    ),
    PreguntaFaq(
        72,
        "Metodología",
        "¿Cómo describo el plan de análisis cuantitativo?",
        "Indica el software (SPSS, Jamovi, R, Excel), la " +
                "estadística descriptiva (frecuencias, medias, " +
                "desviaciones) y la inferencial según la hipótesis, " +
                "previa prueba de normalidad (por ejemplo, Pearson o " +
                "Spearman para correlaciones; t de Student o U de " +
                "Mann-Whitney para comparar dos grupos; chi cuadrado " +
                "para variables categóricas)."
    ),
    PreguntaFaq(
        73,
        "Metodología",
        "¿Cómo describo el plan de análisis cualitativo?",
        "Indica la técnica (análisis de contenido, análisis " +
                "temático, codificación abierta, axial y selectiva), " +
                "los pasos de transcripción y codificación, y el " +
                "software si lo usas (Atlas.ti, NVivo, MAXQDA o " +
                "Taguette)."
    ),
    PreguntaFaq(
        74,
        "Metodología",
        "¿Qué es la triangulación?",
        "Contrastar la información desde distintas fuentes, " +
                "técnicas, investigadores o teorías para fortalecer " +
                "la credibilidad de los hallazgos."
    ),
    PreguntaFaq(
        75,
        "Metodología",
        "¿Qué resguardos éticos debo considerar?",
        "Consentimiento informado (y asentimiento si hay " +
                "menores de edad, con autorización de sus adultos " +
                "responsables), participación voluntaria, derecho a " +
                "retirarse, confidencialidad y anonimato, uso " +
                "exclusivo de los datos para el estudio y " +
                "almacenamiento seguro." +
                "\n\n" +
                "Al final de este capítulo o en las conclusiones: " +
                "tamaño o tipo de muestra, instrumento autoaplicado, " +
                "sesgo de deseabilidad, diseño transversal que impide " +
                "hablar de causalidad." +
                "\n\n" +
                "Por qué ese enfoque y diseño, cómo elegiste la " +
                "muestra, cómo validaste el instrumento, por qué esa " +
                "prueba estadística o técnica de análisis, y cómo " +
                "resguardaste la ética."
    ),
    PreguntaFaq(
        76,
        "Análisis",
        "¿Cómo ordeno el capítulo?",
        "Por objetivos específicos (o por hipótesis o " +
                "categorías). Primero una caracterización de la " +
                "muestra o de los participantes, luego un apartado " +
                "por objetivo y al final la discusión."
    ),
    PreguntaFaq(
        77,
        "Análisis",
        "¿Resultados, análisis y discusión son lo mismo?",
        "No. Los resultados son los datos obtenidos; el " +
                "análisis los describe e interpreta; la discusión los " +
                "compara con el marco teórico y los antecedentes. " +
                "Algunas pautas los separan en apartados y otras los " +
                "integran."
    ),
    PreguntaFaq(
        78,
        "Análisis",
        "¿Qué debo hacer antes de analizar?",
        "Preparar los datos: revisar respuestas incompletas o " +
                "inválidas, codificar, construir la base de datos " +
                "(cuantitativo) o transcribir textualmente las " +
                "entrevistas (cualitativo). Informa cuántos casos " +
                "quedaron válidos."
    ),
    PreguntaFaq(
        79,
        "Análisis",
        "¿Qué incluye el análisis descriptivo?",
        "Frecuencias y porcentajes para variables " +
                "categóricas; media, mediana, desviación estándar, " +
                "mínimo y máximo para variables numéricas."
    ),
    PreguntaFaq(
        80,
        "Análisis",
        "¿Cuándo uso estadística inferencial?",
        "Cuando quieres probar hipótesis o generalizar desde " +
                "una muestra probabilística. Si la muestra es por " +
                "conveniencia, puedes usarla, pero advierte que los " +
                "resultados no se generalizan a la población."
    ),
    PreguntaFaq(
        81,
        "Análisis",
        "¿Cómo elijo la prueba estadística correcta?",
        "Según el objetivo (relacionar o comparar), la escala " +
                "de las variables, el número de grupos y si los datos " +
                "siguen una distribución normal. Con normalidad se " +
                "usan pruebas paramétricas; sin ella, no " +
                "paramétricas."
    ),
    PreguntaFaq(
        82,
        "Análisis",
        "¿Cómo interpreto el valor p?",
        "Si p es menor que el nivel de significancia " +
                "(habitualmente 0,05), se rechaza la hipótesis nula. " +
                "Un resultado significativo no indica que la relación " +
                "sea fuerte ni causal."
    ),
    PreguntaFaq(
        83,
        "Análisis",
        "¿Qué es el tamaño del efecto y debo informarlo?",
        "Indica la magnitud de la relación o diferencia (por " +
                "ejemplo, el coeficiente r o la d de Cohen). Es " +
                "recomendable informarlo junto al valor p porque " +
                "muestra la relevancia práctica."
    ),
    PreguntaFaq(
        84,
        "Análisis",
        "¿Cómo presento tablas y gráficos?",
        "Numerados, con título, fuente y nota cuando haga " +
                "falta, según la norma APA. Cada tabla o gráfico se " +
                "comenta en el texto destacando lo principal, sin " +
                "repetir todos los números. Todas las tablas y " +
                "gráficos (figuras) se enlistan en un índice propio " +
                "para tablas y propio para figuras. Independiente del " +
                "índice de contenidos o materias."
    ),
    PreguntaFaq(
        85,
        "Análisis",
        "¿Tabla o gráfico?",
        "Tabla cuando el lector necesita valores exactos; " +
                "gráfico cuando importa ver una tendencia, una " +
                "comparación o una distribución. No muestres los " +
                "mismos datos en ambos formatos."
    ),
    PreguntaFaq(
        86,
        "Análisis",
        "¿Cómo paso de las transcripciones al análisis?",
        "Lee varias veces, asigna códigos a fragmentos con " +
                "sentido, agrupa los códigos en subcategorías y " +
                "categorías, y busca relaciones entre ellas. Registra " +
                "tus decisiones en un libro de códigos. Se pueden " +
                "utilizar número o colores para codificar y " +
                "posteriormente agrupar las similitudes."
    ),
    PreguntaFaq(
        87,
        "Análisis",
        "¿Cómo presento los hallazgos cualitativos?",
        "Por categoría: una descripción interpretativa, citas " +
                "textuales breves de los participantes que la " +
                "ilustren y una síntesis. Se pueden acompañar de " +
                "matrices de significado que ilustren de mejor forma " +
                "las relaciones entre las categorías (redes o mapas " +
                "de categorías)."
    ),
    PreguntaFaq(
        88,
        "Análisis",
        "¿Cuántas citas textuales de participantes incluyo?",
        "Las necesarias para sostener cada interpretación, " +
                "normalmente dos o tres por subcategoría. Las citas " +
                "ilustran el análisis; no lo reemplazan."
    ),
    PreguntaFaq(
        89,
        "Análisis",
        "¿Cómo protejo la identidad en las citas textuales?",
        "Con códigos (E1_que significa entrevistado_ 1, E2, " +
                "Docente A, Docente B, Estudiante 1, Estudiante 2; " +
                "Sujeto 1, Sujeto 2. etc) y eliminando nombres, " +
                "lugares u otros datos que permitan identificar a la " +
                "persona."
    ),
    PreguntaFaq(
        90,
        "Análisis",
        "¿Qué hago con hallazgos que no esperaba?",
        "Si son hallazgos relacionados con el tema y/o los " +
                "objetivos, se incluyen como “categorías emergentes”, " +
                "ya que surgen del propio trabajo de campo. Suelen " +
                "ser de los aportes más valiosos del estudio."
    ),
    PreguntaFaq(
        91,
        "Análisis",
        "¿Cómo escribo la discusión?",
        "Para cada dimensión principal: qué se encontró, con " +
                "qué estudios o teorías coincide, con cuáles difiere " +
                "y por qué podría ser. Cierra con lo que el hallazgo " +
                "aporta a la investigación."
    ),
    PreguntaFaq(
        92,
        "Análisis",
        "¿Qué hago si mis resultados contradicen la teoría o " +
                "mi hipótesis?",
        "Repórtalos tal cual. Un resultado no esperado no es " +
                "un error: explícalo con posibles razones (contexto, " +
                "muestra, instrumento) y relaciónalo con estudios que " +
                "lo respalden."
    ),
    PreguntaFaq(
        93,
        "Análisis",
        "¿Cómo evito sobreinterpretar?",
        "No afirmes causalidad con un diseño correlacional, " +
                "no generalices con muestras no probabilísticas y " +
                "distingue entre lo que dicen los datos y tu " +
                "interpretación. Resguarda siempre la pertinencia y " +
                "coherencia del estudio."
    ),
    PreguntaFaq(
        94,
        "Análisis",
        "¿En qué tiempo verbal redacto?",
        "Hay diversas formas: una alternativa es colocar los " +
                "resultados en pasado (se encontró; los participantes " +
                "señalaron) y las interpretaciones, junto a lo que " +
                "dice la teoría, en presente. Otra opción sería todo " +
                "en tiempo neutral (se expone, se identifica, se " +
                "establece; se concluye, se ha identificado, se ha " +
                "concluido, se ha establecido, etc.)"
    ),
    PreguntaFaq(
        95,
        "Análisis",
        "¿Qué va en anexos y qué en el cuerpo?",
        "En el cuerpo, las tablas y figuras clave para cada " +
                "objetivo. Solo aquellos más relevantes o " +
                "significativos en función de las dimensiones o " +
                "indicadores asociados a ellos. En anexos: " +
                "instrumentos, consentimientos, planillas completas " +
                "de software, transcripciones o matrices extensas."
    ),
    PreguntaFaq(
        96,
        "Análisis",
        "¿Qué suele preguntar la comisión sobre este " +
                "capítulo?",
        "Qué significa un resultado específico, por qué " +
                "usaste esa prueba, qué explica un hallazgo " +
                "inesperado y cómo se relacionan tus resultados con " +
                "los estudios previos que citaste."
    ),
    PreguntaFaq(
        97,
        "Conclusiones",
        "¿Qué son las conclusiones?",
        "La síntesis interpretativa de los hallazgos más " +
                "importantes y su respuesta a la pregunta de " +
                "investigación. No son un resumen de toda la tesis ni " +
                "una repetición de resultados."
    ),
    PreguntaFaq(
        98,
        "Conclusiones",
        "¿Qué apartados suele tener el capítulo?",
        "Conclusiones por objetivo específico, conclusión " +
                "general (respuesta a la pregunta y al objetivo " +
                "general), verificación de hipótesis si las hubo, " +
                "aportes, limitaciones, recomendaciones y " +
                "proyecciones o líneas futuras."
    ),
    PreguntaFaq(
        99,
        "Conclusiones",
        "¿Cómo redacto una conclusión por objetivo?",
        "Retoma el objetivo, indica en qué medida se cumplió " +
                "y expresa el hallazgo principal en pocas frases, con " +
                "su sentido o implicancia. Ejemplo: En relación con " +
                "el objetivo 2, se concluye que…"
    ),
    PreguntaFaq(
        100,
        "Conclusiones",
        "¿Qué pasa si un objetivo no se cumplió por completo?",
        "Decláralo con honestidad y explica por qué (acceso, " +
                "tiempo, tasa de respuesta). Es preferible a forzar " +
                "una conclusión que los datos no sostienen."
    ),
    PreguntaFaq(
        101,
        "Conclusiones",
        "¿Cómo respondo la pregunta de investigación?",
        "De manera explícita y directa, integrando las " +
                "conclusiones específicas en una respuesta global. Si " +
                "alguien leyera solo ese párrafo, debería saber qué " +
                "encontró tu estudio."
    ),
    PreguntaFaq(
        102,
        "Conclusiones",
        "¿Cómo informo la verificación de hipótesis?",
        "Indica si cada hipótesis se acepta o se rechaza, con " +
                "el respaldo estadístico que lo sostiene (prueba, " +
                "valor p) y una breve interpretación."
    ),
    PreguntaFaq(
        103,
        "Conclusiones",
        "¿Qué son los aportes del estudio?",
        "Lo que la tesis agrega: conocimiento sobre un " +
                "contexto no estudiado, confirmación o matiz de una " +
                "teoría, un instrumento nuevo, información útil para " +
                "una institución o política. Deben conectar con los " +
                "elementos argumentados en la justificación contenida " +
                "en el capítulo I."
    ),
    PreguntaFaq(
        104,
        "Conclusiones",
        "¿Por qué debo declarar limitaciones?",
        "Porque muestran rigor y ayudan a leer correctamente " +
                "los resultados. Incluye limitaciones de muestra, " +
                "instrumento, diseño o contexto, y cómo afectan el " +
                "alcance de las conclusiones."
    ),
    PreguntaFaq(
        105,
        "Conclusiones",
        "¿Cómo formulo recomendaciones?",
        "Deben derivarse de los hallazgos y dirigirse a " +
                "destinatarios concretos (institución, profesionales, " +
                "autoridades, familias), con acciones viables. Evita " +
                "recomendaciones genéricas que no se apoyen en tus " +
                "resultados. Elabora una propuesta global que resulte " +
                "factible y que identifique recursos, plazos, " +
                "responsables, líneas de acción, etc."
    ),
    PreguntaFaq(
        106,
        "Conclusiones",
        "¿Qué son las proyecciones o líneas futuras de " +
                "investigación?",
        "Preguntas que surgieron o quedaron abiertas: ampliar " +
                "la muestra, usar otro diseño, estudiar otra " +
                "población o incorporar variables nuevas."
    ),
    PreguntaFaq(
        107,
        "Conclusiones",
        "¿Puedo incluir una reflexión personal o profesional?",
        "Muchas instituciones lo permiten o lo piden (por " +
                "ejemplo, aportes a la formación profesional). Hazlo " +
                "en un apartado breve y separado de las conclusiones " +
                "científicas. Si no lo piden, es recomendable tenerlo " +
                "presente en caso de preguntas en una eventual " +
                "instancia de defensa o sustentación."
    ),
    PreguntaFaq(
        108,
        "Conclusiones",
        "¿Qué errores son comunes en las conclusiones?",
        "Presentar datos o citas nuevos, repetir tablas, " +
                "concluir sobre aspectos que no se estudiaron, " +
                "generalizar más allá de la muestra o desconectarse " +
                "de los objetivos."
    ),
    PreguntaFaq(
        109,
        "Conclusiones",
        "¿Debo citar autores en las conclusiones?",
        "Por lo general no, o muy poco; la comparación con " +
                "autores corresponde a la discusión. Algunas pautas " +
                "aceptan referencias puntuales para situar un aporte."
    ),
    PreguntaFaq(
        110,
        "Conclusiones",
        "¿Qué extensión deben tener?",
        "Suelen ocupar entre 3 y 8 páginas. Lo importante es " +
                "que cada objetivo tenga su conclusión y que la " +
                "pregunta quede respondida."
    ),
    PreguntaFaq(
        111,
        "Conclusiones",
        "¿Cómo verifico que las conclusiones están alineadas " +
                "con todo el trabajo?",
        "Compáralas lado a lado con la matriz de " +
                "consistencia: cada objetivo y pregunta del capítulo " +
                "I debe tener su respuesta aquí, sostenida por un " +
                "resultado del capítulo IV."
    ),
    PreguntaFaq(
        112,
        "Conclusiones",
        "¿Qué suele preguntar la comisión sobre este " +
                "capítulo?",
        "Cuál es el principal aporte, qué harías distinto, " +
                "cómo se aplican tus resultados en la práctica y qué " +
                "investigarías a continuación."
    ),
    PreguntaFaq(
        113,
        "Transversales",
        "¿En qué orden conviene escribir la tesis?",
        "Planteamiento y marco teórico primero, luego " +
                "metodología, recolección, análisis y conclusiones. " +
                "El resumen y la introducción se escriben al final, " +
                "cuando ya conoces los resultados."
    ),
    PreguntaFaq(
        114,
        "Transversales",
        "¿Qué debe contener el resumen o abstract?",
        "En unas 150 a 300 palabras: problema, objetivo, " +
                "metodología (enfoque, muestra, instrumento), " +
                "resultados principales y conclusión, más 3 a 5 " +
                "palabras clave. Muchas carreras lo piden también en " +
                "inglés."
    ),
    PreguntaFaq(
        115,
        "Transversales",
        "¿Qué diferencia hay entre la introducción y el " +
                "planteamiento del problema?",
        "La introducción presenta brevemente el tema, el " +
                "propósito y la estructura de la tesis capítulo a " +
                "capítulo. El planteamiento desarrolla y argumenta el " +
                "problema en profundidad."
    ),
    PreguntaFaq(
        116,
        "Transversales",
        "¿Qué es la matriz de consistencia y por qué es tan " +
                "útil?",
        "Una tabla que alinea problema, preguntas, objetivos, " +
                "hipótesis o supuestos, variables o categorías, " +
                "dimensiones, técnicas e instrumentos. Sirve para " +
                "detectar incoherencias y es un excelente apoyo para " +
                "la defensa."
    ),
    PreguntaFaq(
        117,
        "Transversales",
        "¿Cómo trabajo con mi profesor guía?",
        "Acuerda un calendario de entregas parciales por " +
                "capítulo, envía avances con anticipación y registra " +
                "los comentarios con sus respuestas. Las " +
                "observaciones no resueltas suelen reaparecer en la " +
                "comisión."
    ),
    PreguntaFaq(
        118,
        "Transversales",
        "¿Cómo preparo la defensa?",
        "El instructivo de ESUCAR contempla una presentación " +
                "20 a 25 minutos. La estructura es entregada por los " +
                "docentes y basada en las etapas centrales del método " +
                "científico. Es decir, focaliza problema de " +
                "investigación, objetivos, metodología, resultados " +
                "claves y conclusiones. Se recomienda ensayar al " +
                "menos 4 veces para organizar los tiempos, los " +
                "contenidos y las habilidades de oratoria."
    ),
    PreguntaFaq(
        119,
        "Transversales",
        "¿Qué debo revisar antes de entregar mi trabajo?",
        "• La pregunta de investigación, el objetivo general y el título dicen lo mismo.\n" +
                "• Cada objetivo específico tiene su apartado en el análisis y su conclusión.\n" +
                "• Los verbos de los objetivos coinciden con el alcance y el diseño declarados.\n" +
                "• Cada variable o categoría está definida en el marco teórico y operacionalizada en la metodología.\n" +
                "• El instrumento está validado y su confiabilidad o rigor están informados.\n" +
                "• Los resguardos éticos están descritos y los consentimientos están en anexos.\n" +
                "• Las tablas y figuras están numeradas, tituladas y comentadas en el texto.\n" +
                "• La discusión compara los hallazgos con los antecedentes del marco teórico.\n" +
                "• Las conclusiones no presentan datos nuevos y declaran limitaciones.\n" +
                "• Todas las citas del texto están en las referencias y viceversa, en la misma norma.\n" +
                "• El documento pasó por el software antiplagio y una revisión de ortografía.\n" +
                "• El formato (márgenes, fuente, interlineado, portada, índices) cumple la pauta de la carrera."
    ),
    PreguntaFaq(
        120,
        "Aplicación",
        "¿Mis estados se guardan al cerrar la aplicación?",
        "Sí. Los estados de las etapas se almacenan " +
                "localmente en el dispositivo mediante DataStore."
    ),
    PreguntaFaq(
        121,
        "Aplicación",
        "¿Puedo utilizar la aplicación sin internet?",
        "Las etapas, las preguntas frecuentes y los " +
                "datos locales pueden consultarse sin conexión. " +
                "Las novedades remotas requieren conexión para " +
                "descargar contenido nuevo."
    ),
    PreguntaFaq(
        122,
        "Aplicación",
        "¿Cómo activo el modo de alto contraste?",
        "Entra a Recursos académicos, abre Accesibilidad " +
                "y activa el interruptor Modo de alto contraste."
    )
)

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var categoriaSeleccionada by rememberSaveable {
        mutableStateOf("Todas")
    }

    var preguntaExpandida by rememberSaveable {
        mutableIntStateOf(-1)
    }

    val categorias = remember {
        listOf(
            "Todas",
            "Planteamiento",
            "Marco teórico",
            "Metodología",
            "Análisis",
            "Conclusiones",
            "Transversales",
            "Aplicación"
        )
    }

    val preguntasFiltradas = remember(
        busqueda,
        categoriaSeleccionada
    ) {

        val textoBuscado = normalizarFaq(
            busqueda.trim()
        )

        preguntasFaq.filter { pregunta ->

            val coincideCategoria =
                categoriaSeleccionada == "Todas" ||
                        pregunta.categoria == categoriaSeleccionada

            val contenidoBuscable = normalizarFaq(
                pregunta.pregunta + " " +
                        pregunta.respuesta + " " +
                        pregunta.categoria
            )

            coincideCategoria &&
                    (
                            textoBuscado.isEmpty() ||
                                    contenidoBuscable.contains(textoBuscado)
                            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colores.fondo),

        contentPadding = PaddingValues(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // VOLVER
        item {

            TextButton(onClick = onVolver) {

                Text(
                    text = "← Volver",
                    color = colores.texto
                )
            }
        }

        // ENCABEZADO
        item {

            Surface(
                color = colores.encabezado,

                shape = RoundedCornerShape(24.dp),

                border = if (altoContraste) {
                    BorderStroke(
                        1.dp,
                        colores.borde
                    )
                } else {
                    null
                }
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "ESUCAR · APOYO ACADÉMICO",
                        color = colores.acento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Preguntas frecuentes",
                        color = colores.textoEncabezado,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Encuentra respuestas sobre " +
                                "metodología, formato y " +
                                "uso de la aplicación.",
                        color = colores.textoEncabezado,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }

        // BUSCADOR
        item {

            OutlinedTextField(
                value = busqueda,

                onValueChange = {
                    busqueda = it
                    preguntaExpandida = -1
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Buscar pregunta")
                },

                placeholder = {
                    Text("Escribe una palabra clave")
                },

                singleLine = true,

                shape = RoundedCornerShape(14.dp),

                colors = OutlinedTextFieldDefaults.colors(

                    focusedTextColor = colores.texto,

                    unfocusedTextColor = colores.texto,

                    focusedContainerColor = colores.tarjeta,

                    unfocusedContainerColor = colores.tarjeta,

                    focusedBorderColor = colores.acento,

                    unfocusedBorderColor = colores.borde,

                    focusedLabelColor = colores.texto,

                    unfocusedLabelColor = colores.textoSecundario,

                    focusedPlaceholderColor =
                        colores.textoSecundario,

                    unfocusedPlaceholderColor =
                        colores.textoSecundario,

                    cursorColor = colores.acento
                )
            )
        }

        // CATEGORÍAS
        item {

            Text(
                text = "Categorías",
                color = colores.texto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            LazyRow(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(categorias) { categoria ->

                    val seleccionada =
                        categoriaSeleccionada == categoria

                    FilterChip(
                        selected = seleccionada,

                        onClick = {
                            categoriaSeleccionada = categoria
                            preguntaExpandida = -1
                        },

                        label = {
                            Text(categoria)
                        },

                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = colores.tarjeta,

                            labelColor = colores.texto,

                            selectedContainerColor = if (altoContraste) {
                                Color.Yellow
                            } else {
                                Color(0xFFE8EFE9)
                            },

                            selectedLabelColor = if (altoContraste) {
                                Color.Black
                            } else {
                                colores.texto
                            }
                        ),

                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionada,
                            borderColor = colores.borde,
                            selectedBorderColor = colores.acento,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 2.dp
                        )
                    )
                }
            }
        }

        // TOTAL DE RESULTADOS
        item {

            Text(
                text = "${preguntasFiltradas.size} " +
                        "pregunta(s) encontrada(s)",

                color = colores.texto,

                fontSize = 17.sp,

                fontWeight = FontWeight.Bold
            )
        }

        // SIN RESULTADOS
        if (preguntasFiltradas.isEmpty()) {

            item {

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    color = colores.tarjeta,

                    shape = RoundedCornerShape(18.dp),

                    border = if (altoContraste) {
                        BorderStroke(
                            1.dp,
                            colores.borde
                        )
                    } else {
                        null
                    }
                ) {

                    Text(
                        text = "No encontramos preguntas " +
                                "con esos criterios. " +
                                "Prueba otra palabra o categoría.",

                        modifier = Modifier.padding(20.dp),

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 21.sp
                    )
                }
            }
        }

        // PREGUNTAS EXPANDIBLES
        items(
            items = preguntasFiltradas,
            key = { it.id }
        ) { pregunta ->

            TarjetaPreguntaAccesible(
                pregunta = pregunta,

                expandida =
                    preguntaExpandida == pregunta.id,

                onClick = {

                    preguntaExpandida =
                        if (preguntaExpandida == pregunta.id) {
                            -1
                        } else {
                            pregunta.id
                        }
                }
            )
        }

        // ACLARACIÓN
        item {

            Text(
                text = "Las respuestas son orientativas " +
                        "y forman parte de un prototipo académico. " +
                        "Verifica siempre las instrucciones " +
                        "vigentes de ESUCAR y de tu docente.",

                color = colores.textoSecundario,

                fontSize = 12.sp,

                lineHeight = 19.sp
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}


// ===================================================
// TARJETA EXPANDIBLE
// ===================================================

@Composable
private fun TarjetaPreguntaAccesible(
    pregunta: PreguntaFaq,
    expandida: Boolean,
    onClick: () -> Unit
) {

    val colores = LocalColoresEsucar.current

    val altoContraste = colores.fondo == Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        colors = CardDefaults.cardColors(
            containerColor = colores.tarjeta
        ),

        shape = RoundedCornerShape(18.dp),

        border = if (altoContraste) {
            BorderStroke(
                1.dp,
                colores.borde
            )
        } else {
            null
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = pregunta.categoria.uppercase(Locale.ROOT),

                color = if (altoContraste) {
                    Color.Yellow
                } else {
                    Color(0xFF896A2C)
                },

                fontSize = 11.sp,

                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = pregunta.pregunta,

                    modifier = Modifier.weight(1f),

                    color = colores.texto,

                    fontSize = 16.sp,

                    lineHeight = 23.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (expandida) "−" else "+",

                    color = if (altoContraste) {
                        Color.Yellow
                    } else {
                        colores.texto
                    },

                    fontSize = 24.sp,

                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = expandida
            ) {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    HorizontalDivider(
                        color = colores.borde
                    )

                    Text(
                        text = pregunta.respuesta,

                        color = colores.textoSecundario,

                        fontSize = 14.sp,

                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}
