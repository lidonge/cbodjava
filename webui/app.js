const endpointInput = document.getElementById("endpoint");
const actionInput = document.getElementById("action");
const idInput = document.getElementById("customer-id");
const nameInput = document.getElementById("customer-name");
const phoneInput = document.getElementById("customer-phone");
const addressInput = document.getElementById("customer-address");
const requestPreview = document.getElementById("request-preview");
const responsePreview = document.getElementById("response-preview");
const messageStrip = document.getElementById("message-strip");
const sendButton = document.getElementById("send-request");
const clearButton = document.getElementById("clear-log");

const presets = {
    "query-seed": {
        action: "Q",
        id: "0000000001",
        name: "",
        phone: "",
        address: ""
    },
    "add-demo": {
        action: "A",
        id: "0000000099",
        name: "BOB",
        phone: "13900000000",
        address: "BEIJING"
    },
    "update-demo": {
        action: "U",
        id: "0000000099",
        name: "BOB-UPDATED",
        phone: "13911112222",
        address: "SHENZHEN"
    },
    "delete-demo": {
        action: "D",
        id: "0000000099",
        name: "",
        phone: "",
        address: ""
    }
};

function buildPayload() {
    return {
        "SCR-ACTION": actionInput.value.trim(),
        "SCR-ID": idInput.value.trim(),
        "SCR-NAME": nameInput.value.trim() || null,
        "SCR-PHONE": phoneInput.value.trim() || null,
        "SCR-ADDRESS": addressInput.value.trim() || null
    };
}

function refreshRequestPreview() {
    requestPreview.textContent = JSON.stringify(buildPayload(), null, 2);
}

function setMessage(text, isError = false) {
    messageStrip.textContent = text;
    messageStrip.classList.toggle("error", isError);
}

async function sendRequest() {
    const endpoint = endpointInput.value.trim();
    const payload = buildPayload();

    refreshRequestPreview();

    if (!endpoint) {
        setMessage("请先填写接口地址。", true);
        return;
    }

    if (!payload["SCR-ACTION"] || !payload["SCR-ID"]) {
        setMessage("动作和客户号是必填项。", true);
        return;
    }

    sendButton.disabled = true;
    sendButton.textContent = "请求中...";
    setMessage("正在调用后端服务...");

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const text = await response.text();
        let data = text;

        try {
            data = JSON.parse(text);
        } catch (ignore) {
        }

        responsePreview.textContent = typeof data === "string"
            ? data
            : JSON.stringify(data, null, 2);

        if (!response.ok) {
            setMessage("请求失败，HTTP " + response.status, true);
            return;
        }

        setMessage("调用完成，后端已返回结果。");
    } catch (error) {
        responsePreview.textContent = String(error);
        setMessage("调用异常：" + error.message, true);
    } finally {
        sendButton.disabled = false;
        sendButton.textContent = "发送请求";
    }
}

function applyPreset(name) {
    const preset = presets[name];
    if (!preset) {
        return;
    }

    actionInput.value = preset.action;
    idInput.value = preset.id;
    nameInput.value = preset.name;
    phoneInput.value = preset.phone;
    addressInput.value = preset.address;
    refreshRequestPreview();
    setMessage("已填充样例，可以直接发送。");
}

function clearOutput() {
    responsePreview.textContent = "尚未发送请求";
    setMessage("已清空结果。");
}

[actionInput, idInput, nameInput, phoneInput, addressInput].forEach((element) => {
    element.addEventListener("input", refreshRequestPreview);
});

document.querySelectorAll(".preset").forEach((button) => {
    button.addEventListener("click", () => applyPreset(button.dataset.preset));
});

sendButton.addEventListener("click", sendRequest);
clearButton.addEventListener("click", clearOutput);

refreshRequestPreview();
